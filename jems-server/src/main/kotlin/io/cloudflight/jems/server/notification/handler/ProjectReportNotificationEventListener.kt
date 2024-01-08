package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

data class ProjectReportStatusChanged(
    val context: Any,
    val projectReportSummary: ProjectReportSubmissionSummary,
    val previousReportStatus: ProjectReportStatus
)

data class ProjectReportDoneByJs(
    val context: Any,
    val projectReportSummary: ProjectReportModel
)

@Service
data class ProjectReportNotificationEventListener(
    private val notificationProjectService: GlobalProjectNotificationServiceInteractor,
    private val securityService: SecurityService,
    private val projectPersistence: ProjectPersistence
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

    private fun ProjectReportStatusChanged.type() = projectReportSummary.status.toNotificationType(previousReportStatus)

    private fun ProjectReportStatusChanged.projectReportVariables(): Map<NotificationVariable, Any> {
        val reportingPeriod = getProjectPeriod(
            projectReportSummary.projectId,
            projectReportSummary.version,
            projectReportSummary.periodNumber
        )
        return mapOf(
            NotificationVariable.ProjectId to projectReportSummary.projectId,
            NotificationVariable.ProjectIdentifier to projectReportSummary.projectIdentifier,
            NotificationVariable.ProjectAcronym to projectReportSummary.projectAcronym,
            NotificationVariable.ProjectReportId to projectReportSummary.id,
            NotificationVariable.ProjectReportNumber to projectReportSummary.reportNumber,
            NotificationVariable.UserName to securityService.currentUser!!.user.email,
            NotificationVariable.ReportingPeriodNumber to (reportingPeriod?.number ?: ""),
            NotificationVariable.ReportingPeriodStart to (reportingPeriod?.start ?: ""),
            NotificationVariable.ReportingPeriodEnd to (reportingPeriod?.end ?: "")
        )
    }

    private fun ProjectReportDoneByJs.projectReportVariables(): Map<NotificationVariable, Any> {
        val reportingPeriod = getProjectPeriod(
            projectReportSummary.projectId,
            projectReportSummary.linkedFormVersion,
            projectReportSummary.periodNumber
        )
        return mapOf(
            NotificationVariable.ProjectId to projectReportSummary.projectId,
            NotificationVariable.ProjectIdentifier to projectReportSummary.projectIdentifier,
            NotificationVariable.ProjectAcronym to projectReportSummary.projectAcronym,
            NotificationVariable.ProjectReportId to projectReportSummary.id,
            NotificationVariable.ProjectReportNumber to projectReportSummary.reportNumber,
            NotificationVariable.UserName to securityService.currentUser!!.user.email,
            NotificationVariable.ReportingPeriodNumber to (reportingPeriod?.number ?: ""),
            NotificationVariable.ReportingPeriodStart to (reportingPeriod?.start ?: ""),
            NotificationVariable.ReportingPeriodEnd to (reportingPeriod?.end ?: "")
        )
    }

    private fun getProjectPeriod(projectId: Long, version: String, periodNumber: Int?): ProjectPeriod? {
        return if (periodNumber != null) {
            val periods = projectPersistence.getProjectPeriods(projectId, version)
            periods.firstOrNull { it.number == periodNumber }
        } else null
    }

}
