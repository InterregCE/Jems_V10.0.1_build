package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.server.notification.model.NotificationType

data class ProjectNotificationConfiguration(
    val id: NotificationType,
    val active: Boolean,
    val sendToManager: Boolean,
    val sendToLeadPartner: Boolean,
    val sendToProjectPartners: Boolean,
    val sendToProjectAssigned: Boolean,
    val emailSubject: String = "",
    val emailBody: String = ""
)
