package io.cloudflight.jems.server.call.service.get_project_notification_configuration

import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration

interface GetProjectNotificationConfigurationsInteractor {
    fun get(callId: Long): List<ProjectNotificationConfiguration>
}
