package io.cloudflight.jems.api.call.dto.notificationConfiguration

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO

data class ProjectNotificationConfigurationDTO(
    val id: ApplicationStatusDTO,
    val active: Boolean,
    val sendToManager: Boolean,
    val sendToLeadPartner: Boolean,
    val sendToProjectPartners: Boolean,
    val sendToProjectAssigned: Boolean,
    val emailSubject: String = "",
    val emailBody: String = ""
)
