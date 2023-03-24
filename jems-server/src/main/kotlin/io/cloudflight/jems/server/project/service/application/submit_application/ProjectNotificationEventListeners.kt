package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProjectBase
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType.Companion.toNotificationType
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectStatusChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

data class ProjectNotificationEvent(
    val context: Any,
    val projectSummary: ProjectSummary,
    val newStatus: ApplicationStatus
)

@Service
data class ProjectNotificationEventListeners(
    private val eventPublisher: ApplicationEventPublisher,
    private val notificationProjectService: GlobalProjectNotificationServiceInteractor,
) {

    @TransactionalEventListener
    fun storeAudit(event: ProjectNotificationEvent) =
        eventPublisher.publishEvent(
            projectStatusChanged(
                event.projectSummary,
                newStatus = event.newStatus
            )
        )

    @EventListener
    fun sendNotifications(event: ProjectNotificationEvent) {
        val type = event.type()
        if (type != null && type.isProjectNotification())
            notificationProjectService.sendNotifications(type, event.project())
    }

    private fun ProjectNotificationEvent.type() = newStatus.toNotificationType()
    private fun ProjectNotificationEvent.project() = NotificationProjectBase(
        projectId = projectSummary.id,
        projectIdentifier = projectSummary.customIdentifier,
        projectAcronym = projectSummary.acronym,
    )

}
