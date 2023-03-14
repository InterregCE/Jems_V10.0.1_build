package io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectNotificationConfigurations

import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration

interface UpdateProjectNotificationConfigurationsInteractor {
    fun update(callId: Long, projectNotificationConfigurations: List<ProjectNotificationConfiguration>): List<ProjectNotificationConfiguration>
}
