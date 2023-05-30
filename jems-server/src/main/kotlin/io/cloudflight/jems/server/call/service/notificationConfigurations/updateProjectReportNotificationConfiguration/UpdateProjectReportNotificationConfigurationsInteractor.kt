package io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectReportNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration

interface UpdateProjectReportNotificationConfigurationsInteractor {
    fun update(callId: Long, projectNotificationConfigurations: List<ProjectNotificationConfiguration>): List<ProjectNotificationConfiguration>
}
