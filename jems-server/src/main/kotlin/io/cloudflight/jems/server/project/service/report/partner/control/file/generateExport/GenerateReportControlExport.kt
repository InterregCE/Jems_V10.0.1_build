package io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GenerateReportControlExport(
    private val partnerPersistence: PartnerPersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportControlExportService: ReportControlExportService,
) : GenerateReportControlExportInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(GenerateReportControlExportException::class)
    override fun export(partnerId: Long, reportId: Long, pluginKey: String) {

        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId, report.version)

        reportControlExportService.generate(report, partnerId, projectId, pluginKey)
    }

}
