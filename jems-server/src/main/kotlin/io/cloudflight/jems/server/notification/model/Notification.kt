package io.cloudflight.jems.server.notification.model

data class Notification(
    val email: String,
    val subject: String,
    val body: String,
    val type: NotificationType,
    val project: NotificationProject?,
)
