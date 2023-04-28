package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProjectBase
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

data class ProjectStatusChangeEvent(
    val context: Any,
    val projectSummary: ProjectSummary,
    val newStatus: ApplicationStatus
)

@Service
data class ProjectNotificationEventListener(
    private val eventPublisher: ApplicationEventPublisher,
    private val notificationProjectService: GlobalProjectNotificationServiceInteractor,
) {

    @TransactionalEventListener
    fun storeAudit(event: ProjectStatusChangeEvent) =
        eventPublisher.publishEvent(
            projectStatusChanged(
                event.projectSummary,
                newStatus = event.newStatus
            )
        )

    @EventListener
    fun sendNotifications(event: ProjectStatusChangeEvent) {
        val type = event.type()
        if (type != null && type.isProjectNotification())
            notificationProjectService.sendNotifications(type, event.project())
    }

    private fun ProjectStatusChangeEvent.type() = newStatus.toNotificationType(projectSummary.status)

    private fun ProjectStatusChangeEvent.project() = NotificationProjectBase(
        projectId = projectSummary.id,
        projectIdentifier = projectSummary.customIdentifier,
        projectAcronym = projectSummary.acronym,
    )
}
