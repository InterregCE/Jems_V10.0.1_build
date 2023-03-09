package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

enum class ProjectNotificationSetting(val id: ApplicationStatus) {
    STEP1_SUBMITTED(ApplicationStatus.STEP1_SUBMITTED),
    SUBMITTED(ApplicationStatus.SUBMITTED);
    companion object {
        val getDefaultProjectNotificationConfigurations =
            values().map{
                ProjectNotificationConfiguration(
                    it.id,
                    active = false,
                    sendToManager = false,
                    sendToLeadPartner = false,
                    sendToProjectPartners = false,
                    sendToProjectAssigned = false,
                    emailSubject = null,
                    emailBody = null
                )
            }
        }
}
