package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

data class PartnerReportStatusChanged(
    val context: Any,
    val projectSummary: ProjectSummary,
    val partnerReportSummary: ProjectPartnerReportSubmissionSummary,
    val previousReportStatus: ReportStatus
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
                variables = event.partnerReportVariables()
            )
    }

    private fun PartnerReportStatusChanged.type() = partnerReportSummary.status.toNotificationType(previousReportStatus)

    private fun PartnerReportStatusChanged.partnerReportVariables() = mapOf(
        NotificationVariable.ProjectId to projectSummary.id,
        NotificationVariable.ProjectIdentifier to projectSummary.customIdentifier,
        NotificationVariable.ProjectAcronym to projectSummary.acronym,
        NotificationVariable.PartnerId to partnerReportSummary.partnerId,
        NotificationVariable.PartnerRole to partnerReportSummary.partnerRole,
        NotificationVariable.PartnerNumber to partnerReportSummary.partnerNumber,
        NotificationVariable.PartnerAbbreviation to partnerReportSummary.partnerAbbreviation,
        NotificationVariable.PartnerReportId to partnerReportSummary.id,
        NotificationVariable.PartnerReportNumber to partnerReportSummary.reportNumber,
    )

}
