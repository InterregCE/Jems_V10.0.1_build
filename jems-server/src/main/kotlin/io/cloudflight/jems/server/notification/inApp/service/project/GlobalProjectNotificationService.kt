package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.event.JemsAsyncMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.service.model.UserEmailNotification
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.UUID

@Service
class GlobalProjectNotificationService(
    private val recipientsService: ProjectNotificationRecipientServiceInteractor,
    private val projectPersistence: ProjectPersistence,
    private val callPersistence: CallPersistence,
    private val callNotificationConfigPersistence: CallNotificationConfigurationsPersistence,
    private val notificationPersistence: NotificationPersistence,
    private val eventPublisher: ApplicationEventPublisher,
    private val programmeDataPersistence: ProgrammeDataPersistence,
) : GlobalProjectNotificationServiceInteractor {

    companion object {
        private fun buildSystemNotificationFor(
            subject: String,
            body: String,
            id: UUID,
            recipients: Set<String>,
        ) = NotificationInApp(
            subject = subject,
            body = body,
            type = NotificationType.SystemMessage,
            time = ZonedDateTime.now(),
            templateVariables = emptyMap(),
            recipientsInApp = recipients,
            recipientsEmail = emptySet(),
            emailTemplate = null,
            groupId = id,
        )
    }

    @Transactional
    override fun sendNotifications(type: NotificationType, variables: Map<NotificationVariable, Any>) {
        validateVariables(variables, type)
        when {
            type.isProjectNotification()
                    || type.isProjectReportNotification()
                    || type.isProjectFileNotification()
                    || type.isProjectReportFileNotification() -> sendProjectNotification(type, variables)
            type.isPartnerReportNotification()
                    || type.isPartnerReportFileNotification() -> sendPartnerReportNotification(type, variables)
            else -> Unit
        }
    }

    @Transactional
    override fun sendSystemNotification(subject: String, body: String, id: UUID) {
        val recipients = recipientsService.getSystemAdminEmails().keys
        val notification = buildSystemNotificationFor(subject = subject, body = body, id, recipients)
        notificationPersistence.saveOrUpdateSystemNotification(notification)
    }

    fun sendProjectNotification(type: NotificationType, variables: Map<NotificationVariable, Any>) {

        val projectId = variables[NotificationVariable.ProjectId] as Long
        val notificationConfig = getNotificationConfiguration(type, projectId) ?: return

        val emailsToNotify = recipientsService.getEmailsForProjectManagersAndAssignedUsers(notificationConfig, projectId)
            .plus(recipientsService.getEmailsForPartners(notificationConfig, projectId))

        sendInAppAndEmails(notificationConfig, emailsToNotify, variables)
    }

    fun sendPartnerReportNotification(type: NotificationType, variables: Map<NotificationVariable, Any>) {

        val projectId = variables[NotificationVariable.ProjectId] as Long
        val notificationConfig = getNotificationConfiguration(type, projectId) ?: return

        val partnerId = variables[NotificationVariable.PartnerId] as Long
        val emailsToNotify = recipientsService.getEmailsForProjectManagersAndAssignedUsers(notificationConfig, projectId)
            .plus(recipientsService.getEmailsForSpecificPartner(notificationConfig, projectId, partnerId))
            .plus(recipientsService.getEmailsForPartnerControllers(notificationConfig, partnerId))

        sendInAppAndEmails(notificationConfig, emailsToNotify, variables)
    }

    private fun getNotificationConfiguration(type: NotificationType, projectId: Long): ProjectNotificationConfiguration? =
        callNotificationConfigPersistence.getActiveNotificationOfType(
            callId = projectPersistence.getCallIdOfProject(projectId),
            type = type,
        )

    private fun sendInAppAndEmails(
        config: ProjectNotificationConfiguration,
        emails: Map<String, UserEmailNotification>,
        variables: Map<NotificationVariable, Any>,
    ) {
        val inAppNotifications = config.toInAppNotificationsFor(emails, variables, groupId = UUID.randomUUID())
        notificationPersistence.saveNotification(inAppNotifications)
        eventPublisher.publishEvent(inAppNotifications.buildSendEmailEvent())
    }

    private fun validateVariables(variables: Map<NotificationVariable, Any>, type: NotificationType) {
        val minimumNeeded = when {
            type.isProjectNotification() -> NotificationVariable.projectNotificationVariables
            type.isProjectReportNotification() -> NotificationVariable.projectReportNotificationVariables
            type.isPartnerReportNotification() -> NotificationVariable.partnerReportNotificationVariables
            type.isProjectFileNotification() -> NotificationVariable.projectFileNotificationVariables
            type.isPartnerReportFileNotification() -> NotificationVariable.partnerReportFileNotificationVariables
            type.isProjectReportFileNotification() -> NotificationVariable.projectReportFileNotificationVariables
            else -> emptySet()
        }

        if (!variables.keys.containsAll(minimumNeeded) || minimumNeeded.isEmpty())
            throw IllegalArgumentException("Provided variables: $variables, does not all the minimum needed ones: $minimumNeeded")
    }

    private fun getCallVariablesFrom(projectId: Long): Map<NotificationVariable, Any> {
        val callId = projectPersistence.getCallIdOfProject(projectId = projectId)
        val callName = callPersistence.getCallSummaryById(callId = callId).name
        return mapOf(
            NotificationVariable.CallId to callId,
            NotificationVariable.CallName to callName,
        )
    }

    private fun getProgrammeVariables(): Map<NotificationVariable, Any> {
        return mapOf(
            NotificationVariable.ProgrammeName to (programmeDataPersistence.getProgrammeName() ?: "")
        )
    }

    private fun String.replacePlaceholdersWith(variables: Map<NotificationVariable, Any>) = variables.entries
        .map { (key, value) -> "[${key.variable}]" to value.toString() }
        .map { (pattern, value) -> { str: String -> str.replace(pattern, value) } }
        .fold(this) { acc, replaceFun -> replaceFun(acc) }

    private fun ProjectNotificationConfiguration.toInAppNotificationsFor(
        users: Map<String, UserEmailNotification>,
        variablesProvided: Map<NotificationVariable, Any>,
        groupId: UUID,
    ): NotificationInApp {
        val projectId = variablesProvided[NotificationVariable.ProjectId] as Long
        val variables = variablesProvided + getCallVariablesFrom(projectId) + getProgrammeVariables()
        return NotificationInApp(
            subject = emailSubject.replacePlaceholdersWith(variables),
            body = emailBody.replacePlaceholdersWith(variables),
            type = id,
            time = ZonedDateTime.now(),
            templateVariables = variables.mapKeys { it.key.variable },
            recipientsInApp = users.keys,
            recipientsEmail = users.filterValues { it.isEmailEnabled && it.isActive() }.keys,
            emailTemplate = "notification.html",
            groupId = groupId,
        )
    }

    private fun NotificationInApp.buildSendEmailEvent() = JemsAsyncMailEvent(
        emailTemplateFileName = emailTemplate!!,
        mailNotificationInfo = MailNotificationInfo(
            subject = subject,
            templateVariables = templateVariables.plus("body" to body).map { Variable(it.key, it.value) }.toSet(),
            recipients = recipientsEmail,
            messageType = type.name,
        ),
    )
}
