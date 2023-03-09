package io.cloudflight.jems.server.call.service.update_project_notification_configurations

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectNotificationConfiguration(private val persistence: CallPersistence) :
    UpdateProjectNotificationConfigurationsInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UpdateProjectNotificationConfigurationsException::class)
    override fun update(
        callId: Long,
        projectNotificationConfigurations: List<ProjectNotificationConfiguration>
    ) =
        ifConfigurationIsValid(projectNotificationConfigurations).run {
            persistence.saveProjectNotificationConfigurations(callId, projectNotificationConfigurations)
        }


    private fun ifConfigurationIsValid(
        projectNotificationConfigurations: List<ProjectNotificationConfiguration>
    ) {
        with(projectNotificationConfigurations.filter {
            !listOf(ApplicationStatus.SUBMITTED, ApplicationStatus.STEP1_SUBMITTED).contains(it.id)
        }) {
            if (this.isNotEmpty())
                throw InvalidNotificationTypeException()
        }
    }
}
