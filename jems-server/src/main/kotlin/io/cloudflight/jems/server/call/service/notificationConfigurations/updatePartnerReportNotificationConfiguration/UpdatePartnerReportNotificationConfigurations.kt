package io.cloudflight.jems.server.call.service.notificationConfigurations.updatePartnerReportNotificationConfiguration

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.Companion.partnerReportNotifications
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.Companion.projectFileControlCommunicationNotifications
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
    ): List<ProjectNotificationConfiguration> {
        validateConfiguration(projectNotificationConfigurations)
        return persistence.saveProjectNotificationConfigurations(callId, projectNotificationConfigurations)
    }

    private fun validateConfiguration(projectNotificationConfigurations: List<ProjectNotificationConfiguration>) {
        val invalidConfigurations = projectNotificationConfigurations
            .filter { it.id !in (partnerReportNotifications union projectFileControlCommunicationNotifications) }

        if (invalidConfigurations.isNotEmpty())
            throw InvalidNotificationTypeException(invalidConfigurations)
    }

}
