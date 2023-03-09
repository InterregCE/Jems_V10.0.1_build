package io.cloudflight.jems.server.call.service.update_project_notification_configurations

import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration

interface UpdateProjectNotificationConfigurationsInteractor {
    fun update(callId: Long, projectNotificationConfigurations: List<ProjectNotificationConfiguration>): List<ProjectNotificationConfiguration>
}
