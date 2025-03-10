package io.cloudflight.jems.server.call.repository.notifications.project

import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.call.repository.toModel
import io.cloudflight.jems.server.call.repository.toNotificationEntity
import io.cloudflight.jems.server.call.repository.toNotificationModel
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CallNotificationConfigurationsPersistenceProvider(
    private val projectNotificationConfigurationRepository: ProjectNotificationConfigurationRepository,
    private val callRepository: CallRepository,
) : CallNotificationConfigurationsPersistence {

    @Transactional(readOnly = true)
    override fun getProjectNotificationConfigurations(callId: Long): List<ProjectNotificationConfiguration> =
        projectNotificationConfigurationRepository.findByIdCallEntityId(callId).toNotificationModel()

    @Transactional
    override fun saveProjectNotificationConfigurations(
        callId: Long,
        projectNotificationConfigurations: List<ProjectNotificationConfiguration>
    ): List<ProjectNotificationConfiguration> {
        val callEntity = callRepository.getReferenceById(callId)

        return projectNotificationConfigurationRepository
            .saveAll(projectNotificationConfigurations.toNotificationEntity(callEntity))
            .toNotificationModel()
    }

    @Transactional
    override fun getActiveNotificationOfType(callId: Long, type: NotificationType) =
        projectNotificationConfigurationRepository.findByActiveTrueAndIdCallEntityIdAndIdId(callId, type)
            ?.toModel()

}
