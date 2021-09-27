package io.cloudflight.jems.server.notification.mail.service.model

import io.cloudflight.jems.server.common.model.Variable

data class MailNotificationInfo(
    val subject: String,
    val templateVariables: Set<Variable>,
    val recipients: Set<String>,
    val messageType: String
)
