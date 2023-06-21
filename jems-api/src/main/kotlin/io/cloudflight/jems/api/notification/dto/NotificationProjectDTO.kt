package io.cloudflight.jems.api.notification.dto

data class NotificationProjectDTO(
    val callId: Long,
    val callName: String,

    val projectId: Long,
    val projectIdentifier: String,
    val projectAcronym: String,
)
