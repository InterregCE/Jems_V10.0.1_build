package io.cloudflight.jems.api.notification.dto

import java.time.ZonedDateTime

data class NotificationDTO(
    val id: Long,
    val project: NotificationProjectDTO?,
    val time: ZonedDateTime,
    val subject: String,
    val body: String,
    val type: NotificationTypeDTO,
)
