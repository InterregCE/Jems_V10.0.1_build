package io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectReportNotificationConfiguration

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
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
    ) =
        ifConfigurationIsValid(projectNotificationConfigurations).run {
            persistence.saveProjectNotificationConfigurations(callId, projectNotificationConfigurations)
        }


    private fun ifConfigurationIsValid(projectNotificationConfigurations: List<ProjectNotificationConfiguration>) =
        with(projectNotificationConfigurations.filter { it.id.isNotProjectReportNotification() }) {
            if (this.isNotEmpty())
                throw InvalidNotificationTypeException()
        }
}
