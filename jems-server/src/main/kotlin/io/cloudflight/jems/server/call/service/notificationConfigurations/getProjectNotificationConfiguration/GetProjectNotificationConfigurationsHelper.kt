package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType

val getDefaultProjectNotificationConfigurations = NotificationType.projectNotifications.map {
    ProjectNotificationConfiguration(
        it,
        active = false,
        sendToManager = false,
        sendToLeadPartner = false,
        sendToProjectPartners = false,
        sendToProjectAssigned = false
    )
}
