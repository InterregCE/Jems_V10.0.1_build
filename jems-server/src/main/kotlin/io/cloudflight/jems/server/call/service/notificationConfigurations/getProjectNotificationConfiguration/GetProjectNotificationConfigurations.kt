package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.model.NotificationType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectNotificationConfigurations(private val persistence: CallNotificationConfigurationsPersistence) :
    GetProjectNotificationConfigurationsInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectNotificationConfigurationException::class)
    override fun get(callId: Long): List<ProjectNotificationConfiguration> {
        val savedNotifications = persistence.getProjectNotificationConfigurations(callId).associateBy { it.id }
        return getDefaultProjectNotificationConfigurations.mergeWith(savedNotifications)
    }

    private fun List<ProjectNotificationConfiguration>.mergeWith(
        existing: Map<NotificationType, ProjectNotificationConfiguration>
    ) =
        map {
            if (it.id in existing.keys)
                existing[it.id]!!
            else
                it
        }
}
