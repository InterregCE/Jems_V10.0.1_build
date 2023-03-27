package io.cloudflight.jems.server.notification.inApp.service.model

import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import java.time.ZonedDateTime

data class NotificationConfigurationWithRecipients(
    val recipientsInApp: Set<String>,
    val recipientsEmail: Set<String>,
    val config: ProjectNotificationConfiguration,
    val emailTemplate: String?,
) {

    fun instantiateWith(project: NotificationProjectBase, vararg variable: Variable) = NotificationInApp(
        subject = config.emailSubject,
        body = config.emailBody,
        type = config.id,
        time = ZonedDateTime.now(),
        templateVariables = variable.associate { Pair(it.name, it.value!!) }.plus(mapOf(
            "projectId" to project.projectId,
            "projectIdentifier" to project.projectIdentifier,
            "projectAcronym" to project.projectAcronym,
        )).toMutableMap(),
        recipientsInApp = recipientsInApp,
        recipientsEmail = recipientsEmail,
        emailTemplate = emailTemplate,
    )

}
