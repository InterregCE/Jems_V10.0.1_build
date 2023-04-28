package io.cloudflight.jems.server.call.service.notificationConfigurations.updatePartnerReportNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration

interface UpdatePartnerReportNotificationConfigurationsInteractor {
    fun update(callId: Long, projectNotificationConfigurations: List<ProjectNotificationConfiguration>): List<ProjectNotificationConfiguration>
}
