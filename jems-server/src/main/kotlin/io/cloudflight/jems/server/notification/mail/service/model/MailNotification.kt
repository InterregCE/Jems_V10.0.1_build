package io.cloudflight.jems.server.notification.mail.service.model

data class MailNotification(
    val id: Long = 0,
    val subject: String,
    val body: String,
    val recipients: Set<String> = emptySet(),
    val messageType: String
)
