package io.cloudflight.jems.server.notification.handler

import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

data class PartnerReportStatusChanged(
    val context: Any,
    val projectId: Long,
    val partnerReportSummary: ProjectPartnerReportSubmissionSummary,
    val previousReportStatus: ReportStatus,
)

@Service
data class PartnerReportNotificationEventListener(
    private val notificationProjectService: GlobalProjectNotificationServiceInteractor,
    private val projectPartnerReportIdentificationPersistence: ProjectPartnerReportIdentificationPersistence
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

    private fun PartnerReportStatusChanged.partnerReportVariables(): Map<NotificationVariable, Any> {
        val reportingPeriod = partnerReportSummary.getReportingPeriod()
        return mapOf(
            NotificationVariable.ProjectId to projectId,
            NotificationVariable.ProjectIdentifier to partnerReportSummary.projectIdentifier,
            NotificationVariable.ProjectAcronym to partnerReportSummary.projectAcronym,
            NotificationVariable.PartnerId to partnerReportSummary.partnerId,
            NotificationVariable.PartnerRole to partnerReportSummary.partnerRole,
            NotificationVariable.PartnerNumber to partnerReportSummary.partnerNumber,
            NotificationVariable.PartnerAbbreviation to partnerReportSummary.partnerAbbreviation,
            NotificationVariable.PartnerReportId to partnerReportSummary.id,
            NotificationVariable.PartnerReportNumber to partnerReportSummary.reportNumber,
            NotificationVariable.ReportingPeriodNumber to (reportingPeriod?.number ?: ""),
            NotificationVariable.ReportingPeriodStart to (reportingPeriod?.start ?: ""),
            NotificationVariable.ReportingPeriodEnd to (reportingPeriod?.end ?: "")
        )
    }

    private fun ProjectPartnerReportSubmissionSummary.getReportingPeriod(): ProjectPartnerReportPeriod? {
        return if (periodNumber != null) {
            val periods = projectPartnerReportIdentificationPersistence.getAvailablePeriods(partnerId, id)
            periods.firstOrNull { it.number == periodNumber}
        } else null
    }

}
