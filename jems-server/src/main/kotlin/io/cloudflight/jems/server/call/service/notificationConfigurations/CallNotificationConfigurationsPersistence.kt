package io.cloudflight.jems.server.call.service.notificationConfigurations

import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

interface CallNotificationConfigurationsPersistence {
    fun getProjectNotificationConfigurations(callId: Long): List<ProjectNotificationConfiguration>

    fun saveProjectNotificationConfigurations(
        callId: Long, projectNotificationConfigurations: List<ProjectNotificationConfiguration>
    ): List<ProjectNotificationConfiguration>

    fun getActiveNotificationOfType(callId: Long, status: ApplicationStatus): ProjectNotificationConfiguration?
}