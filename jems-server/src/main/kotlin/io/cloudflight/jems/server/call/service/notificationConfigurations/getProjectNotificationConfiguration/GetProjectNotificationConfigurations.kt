package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectNotificationConfigurations(
    private val persistence: CallNotificationConfigurationsPersistence
) : GetProjectNotificationConfigurationsInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectNotificationConfigurationException::class)
    override fun get(callId: Long): List<ProjectNotificationConfiguration> {
        val savedNotifications = persistence.getProjectNotificationConfigurations(callId).associateBy { it.id }

        val projectNotifications = NotificationType.projectNotifications.sorted().toMutableList()
        projectNotifications.addAll(
            projectNotifications.size - 1, // make Closed type the last one.
            NotificationType.projectFileSharedFolderNotifications.sorted()
        )

        return projectNotifications.map {
            savedNotifications.getOrDefault(it, getDefaultProjectNotificationConfiguration(it))
        }
    }

    private fun getDefaultProjectNotificationConfiguration(type: NotificationType) = ProjectNotificationConfiguration(type)
}
