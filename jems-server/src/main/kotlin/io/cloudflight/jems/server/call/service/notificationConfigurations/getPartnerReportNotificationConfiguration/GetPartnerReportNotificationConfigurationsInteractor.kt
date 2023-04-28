package io.cloudflight.jems.server.call.service.notificationConfigurations.getPartnerReportNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration

interface GetPartnerReportNotificationConfigurationsInteractor {
    fun get(callId: Long): List<ProjectNotificationConfiguration>
}
