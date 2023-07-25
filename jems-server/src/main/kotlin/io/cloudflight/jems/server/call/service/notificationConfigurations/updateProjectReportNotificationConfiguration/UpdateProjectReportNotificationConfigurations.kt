package io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectReportNotificationConfiguration

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.Companion.projectReportNotifications
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportNotificationConfigurations(
    private val persistence: CallNotificationConfigurationsPersistence
) : UpdateProjectReportNotificationConfigurationsInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UpdateProjectReportNotificationConfigurationsException::class)
    override fun update(
        callId: Long,
        projectNotificationConfigurations: List<ProjectNotificationConfiguration>
    ): List<ProjectNotificationConfiguration> {
        validateConfiguration(projectNotificationConfigurations)
        return persistence.saveProjectNotificationConfigurations(callId, projectNotificationConfigurations)
    }

    private fun validateConfiguration(projectNotificationConfigurations: List<ProjectNotificationConfiguration>) {
        val invalidConfigurations = projectNotificationConfigurations
            .filter { it.id !in projectReportNotifications }

        if (invalidConfigurations.isNotEmpty())
            throw InvalidNotificationTypeException(invalidConfigurations)
    }

}
