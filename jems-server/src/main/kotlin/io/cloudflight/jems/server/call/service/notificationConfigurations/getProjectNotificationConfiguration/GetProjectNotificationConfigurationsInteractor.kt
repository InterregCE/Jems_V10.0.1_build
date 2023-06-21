package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration

interface GetProjectNotificationConfigurationsInteractor {
    fun get(callId: Long): List<ProjectNotificationConfiguration>
}
