package io.cloudflight.jems.server.notification.inApp.service.model

import java.time.ZonedDateTime

data class Notification(
    val subject: String,
    val body: String,
    val type: NotificationType,
    val time: ZonedDateTime,
    val project: NotificationProject?,
)
