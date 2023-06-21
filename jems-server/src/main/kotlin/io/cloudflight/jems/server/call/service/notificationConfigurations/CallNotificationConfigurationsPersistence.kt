package io.cloudflight.jems.server.call.service.notificationConfigurations

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType

interface CallNotificationConfigurationsPersistence {
    fun getProjectNotificationConfigurations(callId: Long): List<ProjectNotificationConfiguration>

    fun saveProjectNotificationConfigurations(
        callId: Long, projectNotificationConfigurations: List<ProjectNotificationConfiguration>
    ): List<ProjectNotificationConfiguration>

    fun getActiveNotificationOfType(callId: Long, type: NotificationType): ProjectNotificationConfiguration?

}
