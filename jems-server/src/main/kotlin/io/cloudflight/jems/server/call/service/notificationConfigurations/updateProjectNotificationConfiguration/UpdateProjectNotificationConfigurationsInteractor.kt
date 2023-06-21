package io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration

interface UpdateProjectNotificationConfigurationsInteractor {
    fun update(callId: Long, projectNotificationConfigurations: List<ProjectNotificationConfiguration>): List<ProjectNotificationConfiguration>
}
