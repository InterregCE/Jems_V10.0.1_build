package io.cloudflight.jems.server.notification.inApp.service.model

data class NotificationProject(
    val callId: Long,
    val callName: String,

    val projectId: Long,
    val projectIdentifier: String,
    val projectAcronym: String,
)
