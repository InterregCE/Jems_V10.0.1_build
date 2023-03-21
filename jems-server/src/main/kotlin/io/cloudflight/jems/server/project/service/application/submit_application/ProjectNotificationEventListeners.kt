package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.Notification
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProject
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.Companion.toNotificationType
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener
import java.time.ZonedDateTime

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
    private val userProjectPersistence: UserProjectPersistence,
    private val notificationPersistence: NotificationPersistence,
    private val userPersistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun publishJemsMailEvent(event: ProjectNotificationEvent) {

        val projectId = event.projectSummary.id
        val callId = event.projectSummary.callId
        val notificationType = event.newStatus.toNotificationType()

        val notification = notificationType?.let {
            callNotificationConfigPersistence.getActiveNotificationOfType(callId, notificationType)
        } ?: return

        val managers = if (!notification.sendToManager) emptySet() else
            userProjectCollaboratorPersistence.getUserIdsForProject(projectId).emails()

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
            userProjectPersistence.getUsersForProject(projectId).emails() union getUsersWithProjectRetrievePermissions().emails()

        val emailsToNotify =
            managers union leadPartnerCollaborators union nonLeadPartnerCollaborators union programmeUsers

        val time = ZonedDateTime.now()
        val userNotification = notification.buildNotificationFor(event.projectSummary, time)
        val userEmailNotification = userNotification.buildEmailFor(emailsToNotify)

        notificationPersistence.saveNotifications(userNotification, emailsToNotify)
        eventPublisher.publishEvent(userEmailNotification)
    }

    private fun Set<PartnerCollaborator>.partnerCollaboratorEmails() = mapTo(HashSet()) { it.userEmail }
    private fun Collection<UserSummary>.emails() = mapTo(HashSet()) { it.email }
    private fun List<CollaboratorAssignedToProject>.emails() = mapTo(HashSet()) { it.userEmail }

    private fun ProjectSummary.toProject() = NotificationProject(callId, callName, id, customIdentifier, acronym)

    private fun ProjectNotificationConfiguration.buildNotificationFor(
        projectSummary: ProjectSummary,
        time: ZonedDateTime,
    ) = Notification(
        subject = emailSubject,
        body = emailBody,
        type = id,
        time = time,
        project = projectSummary.toProject(),
    )

    private fun Notification.buildEmailFor(emails: Set<String>) = JemsMailEvent(
        emailTemplateFileName = "notification.html",
        mailNotificationInfo = MailNotificationInfo(
            subject = subject,
            templateVariables =
            setOf(
                Variable("projectId", project!!.projectId),
                Variable("projectIdentifier", project.projectIdentifier),
                Variable("projectAcronym", project.projectAcronym),
                Variable("body", body),
            ),
            recipients = emails,
            messageType = type.toString(),
        ),
    )

    private fun getUsersWithProjectRetrievePermissions() = userPersistence.findAllWithRoleIdIn(
        roleIds = userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = UserRolePermission.getGlobalProjectRetrievePermissions(),
            needsNotToHaveAnyOf = emptySet(),
        )
    )

}
