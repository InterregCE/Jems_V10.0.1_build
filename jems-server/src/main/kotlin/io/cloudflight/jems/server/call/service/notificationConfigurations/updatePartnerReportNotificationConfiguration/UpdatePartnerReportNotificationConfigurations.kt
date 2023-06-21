package io.cloudflight.jems.server.call.service.notificationConfigurations.updatePartnerReportNotificationConfiguration

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePartnerReportNotificationConfigurations(
    private val persistence: CallNotificationConfigurationsPersistence
) : UpdatePartnerReportNotificationConfigurationsInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UpdatePartnerReportNotificationConfigurationsException::class)
    override fun update(
        callId: Long,
        projectNotificationConfigurations: List<ProjectNotificationConfiguration>
    ) =
        ifConfigurationIsValid(projectNotificationConfigurations).run {
            persistence.saveProjectNotificationConfigurations(callId, projectNotificationConfigurations)
        }


    private fun ifConfigurationIsValid(projectNotificationConfigurations: List<ProjectNotificationConfiguration>) =
        with(projectNotificationConfigurations.filter { it.id.isNotPartnerReportNotification() }) {
            if (this.isNotEmpty())
                throw InvalidNotificationTypeException()
        }
}
