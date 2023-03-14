package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

data class ProjectNotificationConfiguration(
    val id: ApplicationStatus,
    val active: Boolean,
    val sendToManager: Boolean,
    val sendToLeadPartner: Boolean,
    val sendToProjectPartners: Boolean,
    val sendToProjectAssigned: Boolean,
    val emailSubject: String = "",
    val emailBody: String = ""
)
