package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProjectBase
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

data class PartnerReportStatusChanged(
    val context: Any,
    val projectSummary: ProjectSummary,
    val partnerReportSummary: ProjectPartnerReportSubmissionSummary
)

@Service
data class PartnerReportNotificationEventListener(
    private val notificationProjectService: GlobalProjectNotificationServiceInteractor,
) {

    @EventListener
    fun sendNotifications(event: PartnerReportStatusChanged) {
        val type = event.type()
        if (type != null && type.isPartnerReportNotification())
            notificationProjectService.sendNotifications(
                type = type,
                project = event.project(),
                Variable("partnerId", event.partnerReportSummary.partnerId),
                Variable("partnerRole", event.partnerReportSummary.partnerRole),
                Variable("partnerNumber", event.partnerReportSummary.partnerNumber),
            )
    }

    private fun PartnerReportStatusChanged.type() = partnerReportSummary.status.toNotificationType()

    private fun PartnerReportStatusChanged.project() = NotificationProjectBase(
        projectId = projectSummary.id,
        projectIdentifier = projectSummary.customIdentifier,
        projectAcronym = projectSummary.acronym,
    )
}
