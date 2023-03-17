package io.cloudflight.jems.server.notification.inApp.service.model

import java.time.ZonedDateTime

data class UserNotification(
    val id: Long,
    val project: NotificationProject?,
    val time: ZonedDateTime,
    val subject: String,
    val body: String,
    val type: NotificationType,
)
