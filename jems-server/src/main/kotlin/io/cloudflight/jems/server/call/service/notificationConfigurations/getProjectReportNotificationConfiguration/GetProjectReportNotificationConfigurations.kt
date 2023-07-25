package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectReportNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportNotificationConfigurations(
    private val persistence: CallNotificationConfigurationsPersistence
) : GetProjectReportNotificationConfigurationsInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportNotificationConfigurationsException::class)
    override fun get(callId: Long): List<ProjectNotificationConfiguration> {
        val savedNotifications = persistence.getProjectNotificationConfigurations(callId).associateBy { it.id }

        return NotificationType.projectReportNotifications.sorted().map {
            savedNotifications.getOrDefault(it, getDefaultProjectNotificationConfiguration(it))
        }
    }

    private fun getDefaultProjectNotificationConfiguration(type: NotificationType) = ProjectNotificationConfiguration(type)
}
