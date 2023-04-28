package io.cloudflight.jems.api.call.dto.notificationConfiguration

import io.cloudflight.jems.api.notification.dto.NotificationTypeDTO

data class ProjectNotificationConfigurationDTO(
    val id: NotificationTypeDTO,
    val active: Boolean,
    val sendToManager: Boolean,
    val sendToLeadPartner: Boolean,
    val sendToProjectPartners: Boolean,
    val sendToProjectAssigned: Boolean,
    val sendToControllers: Boolean,
    val emailSubject: String = "",
    val emailBody: String = ""
)
