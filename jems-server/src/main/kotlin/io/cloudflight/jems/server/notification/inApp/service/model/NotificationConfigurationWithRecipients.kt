package io.cloudflight.jems.server.notification.inApp.service.model

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.common.model.Variable
import java.time.ZonedDateTime

data class NotificationConfigurationWithRecipients(
    val recipientsInApp: Set<String>,
    val recipientsEmail: Set<String>,
    val config: ProjectNotificationConfiguration,
    val emailTemplate: String?,
) {

    fun instantiateWith(variableMap: MutableMap<String, Any>) = NotificationInApp(
        subject = config.emailSubject,
        body = config.emailBody,
        type = config.id,
        time = ZonedDateTime.now(),
        templateVariables = variableMap,
        recipientsInApp = recipientsInApp,
        recipientsEmail = recipientsEmail,
        emailTemplate = emailTemplate,
    )

}
