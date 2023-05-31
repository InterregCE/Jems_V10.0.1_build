package io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport

import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class ExportControlReportOnReportStatusChangedEvent(
    private val reportControlExportService: ReportControlExportService,
    private val reportPersistence: ProjectPartnerReportPersistence,
) {
    companion object {
        private const val controlReportExportPluginKey = "standard-partner-control-report-export-plugin"
        private val logger = LoggerFactory.getLogger(ExportControlReportOnReportStatusChangedEvent::class.java)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun generateStandardControlExport(event: PartnerReportStatusChanged) {
        val reportId = event.partnerReportSummary.id
        val partnerId = event.partnerReportSummary.partnerId
        val projectId = event.projectSummary.id
        if (event.partnerReportSummary.status == ReportStatus.ReOpenCertified) {
            try {
                val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
                reportControlExportService.generate(report, partnerId = partnerId, projectId = projectId, controlReportExportPluginKey)
            } catch (e: RuntimeException) {
                logger.warn(
                    "ReOpen Certified Report: Failed to generate export using plugin with key = $controlReportExportPluginKey " +
                            "for the certified report with id = $reportId"
                )
                logger.error(e.stackTraceToString())
            }
        }
    }
}
