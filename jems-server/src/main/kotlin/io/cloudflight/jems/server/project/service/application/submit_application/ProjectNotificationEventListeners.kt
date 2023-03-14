package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

data class ProjectNotificationEvent(
    val context: Any,
    val projectSummary: ProjectSummary,
    val newStatus: ApplicationStatus
)

@Service
data class ProjectNotificationEventListeners(
    private val eventPublisher: ApplicationEventPublisher,
    private val callNotificationConfigPersistence: CallNotificationConfigurationsPersistence,
    private val userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val userProjectPersistence: UserProjectPersistence
) {

    @TransactionalEventListener
    fun publishJemsAuditEvent(event: ProjectNotificationEvent) =
        eventPublisher.publishEvent(
            projectStatusChanged(
                event.projectSummary,
                newStatus = event.newStatus
            )
        )


    @TransactionalEventListener
    fun publishJemsMailEvent(event: ProjectNotificationEvent) {

        val projectId = event.projectSummary.id
        val callId = event.projectSummary.callId
        val newStatus = event.newStatus

        val notification = callNotificationConfigPersistence.getActiveNotificationOfType(callId, newStatus) ?: return

        val managers = if (!notification.sendToManager) emptySet() else
            userProjectCollaboratorPersistence.getUserIdsForProject(projectId)
                .onlyThoseWithManage().emails()

        val partnerIdsByType = partnerPersistence.findTop30ByProjectId(projectId).groupBy({ it.role }, { it.id })
        val leadPartnerId = partnerIdsByType[ProjectPartnerRole.LEAD_PARTNER]?.firstOrNull()
        val partnerIds = partnerIdsByType[ProjectPartnerRole.PARTNER]?.toSet() ?: emptySet()

        val leadPartnerCollaborators = if (leadPartnerId == null || !notification.sendToLeadPartner) emptySet() else
            partnerCollaboratorPersistence.findByProjectAndPartners(projectId, setOf(leadPartnerId))
                .partnerCollaboratorEmails()

        val nonLeadPartnerCollaborators =
            if (partnerIds.isEmpty() || !notification.sendToProjectPartners) emptySet() else
                partnerCollaboratorPersistence.findByProjectAndPartners(projectId, partnerIds)
                    .partnerCollaboratorEmails()

        val programmeUsers = if (!notification.sendToProjectAssigned) emptySet() else
            userProjectPersistence.getUsersForProject(projectId).emails()

        val emailsToNotify =
            managers union leadPartnerCollaborators union nonLeadPartnerCollaborators union programmeUsers


        eventPublisher.publishEvent(
            JemsMailEvent(
                emailTemplateFileName = "notification.html",
                mailNotificationInfo = MailNotificationInfo(
                    subject = notification.emailSubject!!,
                    templateVariables =
                    setOf(
                        Variable("body", notification.emailBody),
                    ),
                    recipients = emailsToNotify,
                    messageType = notification.id.toString()
                )
            )
        )
    }

    private fun Set<PartnerCollaborator>.partnerCollaboratorEmails() = mapTo(HashSet()) { it.userEmail }
    private fun Set<UserSummary>.emails() = mapTo(HashSet()) { it.email }
    private fun List<CollaboratorAssignedToProject>.emails() = mapTo(HashSet()) { it.userEmail }
    private fun List<CollaboratorAssignedToProject>.onlyThoseWithManage() =
        filter { it.level == ProjectCollaboratorLevel.MANAGE }

}

