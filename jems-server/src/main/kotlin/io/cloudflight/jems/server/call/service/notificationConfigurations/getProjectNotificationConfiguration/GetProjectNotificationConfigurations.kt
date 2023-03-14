package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.model.ProjectNotificationSetting
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectNotificationConfigurations(private val persistence: CallNotificationConfigurationsPersistence) :
    GetProjectNotificationConfigurationsInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectNotificationConfigurationException::class)
    override fun get(callId: Long): List<ProjectNotificationConfiguration> {
        val defaultNotifications = ProjectNotificationSetting.getDefaultProjectNotificationConfigurations
        val savedNotifications = persistence.getProjectNotificationConfigurations(callId).associateBy{ it.id }
        return defaultNotifications.mergeWith(savedNotifications)
    }

    private fun List<ProjectNotificationConfiguration>.mergeWith(
        existing: Map<ApplicationStatus, ProjectNotificationConfiguration>
    ) =
        map {
            if (it.id in existing.keys)
                existing[it.id]!!
            else
                it
        }
}
