package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.event.JemsAsyncMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationConfigurationWithRecipients
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProjectBase
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.UserRolePersistence
import io.cloudflight.jems.server.user.service.model.UserEmailNotification
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GlobalProjectNotificationService(
    private val projectPersistence: ProjectPersistence,
    private val callNotificationConfigPersistence: CallNotificationConfigurationsPersistence,
    private val userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val userProjectPersistence: UserProjectPersistence,
    private val userPersistence: UserPersistence,
    private val userRolePersistence: UserRolePersistence,
    private val notificationPersistence: NotificationPersistence,
    private val eventPublisher: ApplicationEventPublisher,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
) : GlobalProjectNotificationServiceInteractor {

    @Transactional
    override fun sendNotifications(
        type: NotificationType,
        project: NotificationProjectBase,
        vararg extraVariables: Variable,
    ) {
        val variableMap = extraVariables.associate { Pair(it.name, it.value!!) }.toMutableMap()

        resolveNotificationTemplate(type, project.projectId, variableMap["partnerId"] as? Long)?.let { template ->
            variableMap.putAll(
                mapOf(
                    "subject" to template.config.emailSubject,
                    "body" to template.config.emailBody,
                    "projectId" to project.projectId,
                    "projectIdentifier" to project.projectIdentifier,
                    "projectAcronym" to project.projectAcronym,
                )
            )

            val inAppNotification = template.instantiateWith(variableMap)
            notificationPersistence.saveNotification(inAppNotification)
            eventPublisher.publishEvent(inAppNotification.buildSendEmailEvent())
        }
    }


    private fun resolveNotificationTemplate(
        type: NotificationType,
        projectId: Long,
        partnerId: Long?
    ): NotificationConfigurationWithRecipients? {
        val notification = getNotificationConfiguration(type, projectId) ?: return null

        val managers = if (!notification.sendToManager) emptyMap() else
            userProjectCollaboratorPersistence.getUserIdsForProject(projectId).emails()

        val partnerIdsByType = partnerPersistence.findTop30ByProjectId(projectId).groupBy({ it.role }, { it.id })
        val leadPartnerId = partnerIdsByType[ProjectPartnerRole.LEAD_PARTNER]?.firstOrNull()
        val partnerIds = partnerIdsByType[ProjectPartnerRole.PARTNER]?.toSet() ?: emptySet()

        val leadPartnerCollaborators = if (leadPartnerId == null || !notification.sendToLeadPartner) emptyMap() else
            partnerCollaboratorPersistence.findByProjectAndPartners(projectId, setOf(leadPartnerId))
                .partnerCollaboratorEmails()

        val nonLeadPartnerCollaborators =
            if (partnerIds.isEmpty() || !notification.sendToProjectPartners) emptyMap() else
                partnerCollaboratorPersistence.findByProjectAndPartners(projectId, partnerIds)
                    .partnerCollaboratorEmails()

        val programmeUsers = if (!notification.sendToProjectAssigned) emptyMap() else
            userProjectPersistence.getUsersForProject(projectId)
                .emails() + getUsersWithProjectRetrievePermissions().emails()

        val controllerUsers = if (!notification.sendToControllers || type.isNotPartnerReportNotification() || partnerId == null) emptyMap() else
            userPersistence.findAllByIds(controllerInstitutionPersistence.getRelatedUserIdsForPartner(partnerId))
                .emails()

        val emailsToNotify =
            managers + leadPartnerCollaborators + nonLeadPartnerCollaborators + programmeUsers + controllerUsers

        return NotificationConfigurationWithRecipients(
            recipientsInApp = emailsToNotify.keys,
            recipientsEmail = emailsToNotify.filterValues { it.isEmailEnabled && it.isActive() }.keys,
            config = notification,
            emailTemplate = "notification.html",
        )
    }

    private fun getNotificationConfiguration(type: NotificationType, projectId: Long): ProjectNotificationConfiguration? {
        return if (type.isProjectNotification() || type.isPartnerReportNotification()) {
            callNotificationConfigPersistence.getActiveNotificationOfType(
                callId = projectPersistence.getCallIdOfProject(projectId),
                type = type
            )
        } else null
    }

    private fun Set<PartnerCollaborator>.partnerCollaboratorEmails() =
        associateBy({ it.userEmail }, { UserEmailNotification(it.sendNotificationsToEmail, it.userStatus) })

    private fun Collection<UserSummary>.emails() =
        associateBy({ it.email }, { UserEmailNotification(it.sendNotificationsToEmail, it.userStatus) })

    private fun List<CollaboratorAssignedToProject>.emails() =
        associateBy({ it.userEmail }, { UserEmailNotification(it.sendNotificationsToEmail, it.userStatus) })

    private fun getUsersWithProjectRetrievePermissions() = userPersistence.findAllWithRoleIdIn(
        roleIds = userRolePersistence.findRoleIdsHavingAndNotHavingPermissions(
            needsToHaveAtLeastOneFrom = UserRolePermission.getGlobalProjectRetrievePermissions(),
            needsNotToHaveAnyOf = emptySet(),
        )
    )

    private fun NotificationInApp.buildSendEmailEvent() = JemsAsyncMailEvent(
        emailTemplateFileName = emailTemplate!!,
        mailNotificationInfo = MailNotificationInfo(
            subject = subject,
            templateVariables = templateVariables.mapTo(HashSet()) { Variable(it.key, it.value) },
            recipients = recipientsEmail,
            messageType = type.name,
        ),
    )
}
