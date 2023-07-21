package io.cloudflight.jems.server.call.service.notificationConfigurations.getPartnerReportNotificationConfiguration

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerReportNotificationConfigurations(
    private val persistence: CallNotificationConfigurationsPersistence
) : GetPartnerReportNotificationConfigurationsInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPartnerReportNotificationConfigurationsException::class)
    override fun get(callId: Long): List<ProjectNotificationConfiguration> {
        val savedNotifications = persistence.getProjectNotificationConfigurations(callId).associateBy { it.id }

        val partnerReportNotifications = NotificationType.partnerReportNotifications.sorted().toMutableList()
        partnerReportNotifications.addAll(NotificationType.projectFileControlCommunicationNotifications.sorted())

        return partnerReportNotifications.map {
            savedNotifications.getOrDefault(it, getDefaultProjectNotificationConfiguration(it))
        }
    }

    private fun getDefaultProjectNotificationConfiguration(type: NotificationType) = ProjectNotificationConfiguration(type)
}
