package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

data class ProjectReportStatusChanged(
    val context: Any,
    val projectReportSummary: ProjectReportSubmissionSummary,
)

data class ProjectReportDoneByJs(
    val context: Any,
    val projectReportSummary: ProjectReportModel,
)

@Service
data class ProjectReportNotificationEventListener(
    private val notificationProjectService: GlobalProjectNotificationServiceInteractor,
    private val securityService: SecurityService
) {

    @EventListener
    fun sendNotifications(event: ProjectReportStatusChanged) {
        val type = event.type()
        if (type != null && type.isProjectReportNotification())
            notificationProjectService.sendNotifications(
                type = type,
                variables = event.projectReportVariables()
            )
    }

    @EventListener
    fun sendNotifications(event: ProjectReportDoneByJs) {
        notificationProjectService.sendNotifications(
            type = NotificationType.ProjectReportVerificationDoneNotificationSent,
            variables = event.projectReportVariables()
        )
    }

    private fun ProjectReportStatusChanged.type() = projectReportSummary.status.toNotificationType()

    private fun ProjectReportStatusChanged.projectReportVariables() = mapOf(
        NotificationVariable.ProjectId to projectReportSummary.projectId,
        NotificationVariable.ProjectIdentifier to projectReportSummary.projectIdentifier,
        NotificationVariable.ProjectAcronym to projectReportSummary.projectAcronym,
        NotificationVariable.ProjectReportId to projectReportSummary.id,
        NotificationVariable.ProjectReportNumber to projectReportSummary.reportNumber,
        NotificationVariable.UserName to securityService.currentUser!!.user.email
    )

    private fun ProjectReportDoneByJs.projectReportVariables() = mapOf(
        NotificationVariable.ProjectId to projectReportSummary.projectId,
        NotificationVariable.ProjectIdentifier to projectReportSummary.projectIdentifier,
        NotificationVariable.ProjectAcronym to projectReportSummary.projectAcronym,
        NotificationVariable.ProjectReportId to projectReportSummary.id,
        NotificationVariable.ProjectReportNumber to projectReportSummary.reportNumber,
        NotificationVariable.UserName to securityService.currentUser!!.user.email
    )
}
