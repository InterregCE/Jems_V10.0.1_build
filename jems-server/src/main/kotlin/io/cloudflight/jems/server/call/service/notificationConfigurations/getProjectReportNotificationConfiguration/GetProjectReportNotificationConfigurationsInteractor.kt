package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectReportNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration

interface GetProjectReportNotificationConfigurationsInteractor {
    fun get(callId: Long): List<ProjectNotificationConfiguration>
}
