package io.cloudflight.jems.server.call.repository.notifications.project

import io.cloudflight.jems.server.call.entity.ProjectNotificationConfigurationEntity
import io.cloudflight.jems.server.call.entity.ProjectNotificationConfigurationId
import io.cloudflight.jems.server.notification.model.NotificationType
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectNotificationConfigurationRepository : JpaRepository<ProjectNotificationConfigurationEntity, ApplicationStatus> {

    fun findByIdCallEntityId(callId: Long?): List<ProjectNotificationConfigurationEntity>

    fun findByActiveTrueAndIdCallEntityIdAndIdId(callId: Long, type: NotificationType): ProjectNotificationConfigurationEntity?

}
