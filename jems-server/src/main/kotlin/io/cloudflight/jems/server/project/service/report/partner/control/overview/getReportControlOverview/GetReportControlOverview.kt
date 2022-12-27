package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportControlOverview(
    private val controlOverviewPersistence: ProjectPartnerReportControlOverviewPersistence,
    private val reportPersistence: ProjectPartnerReportPersistence
): GetReportControlOverviewInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportControlOverviewException::class)
    override fun get(partnerId: Long, reportId: Long): ControlOverview {
        val overview = controlOverviewPersistence.getPartnerControlReportOverview(partnerId, reportId)
        val reportStatusAndVersion = reportPersistence.getPartnerReportStatusAndVersion(partnerId, reportId)

        if (reportStatusAndVersion.status == ReportStatus.InControl) {
            val lastCertifiedReportId = reportPersistence.getLastCertifiedPartnerReportId(partnerId)
            if (lastCertifiedReportId != null) {
                val lastCertifiedReport = reportPersistence.getPartnerReportById(partnerId, lastCertifiedReportId)
                val lastCertifiedOverview = controlOverviewPersistence.getPartnerControlReportOverview(partnerId, lastCertifiedReportId)
                overview.previousFollowUpMeasuresFromLastReport = lastCertifiedOverview.followUpMeasuresForNextReport
                overview.lastCertifiedReportNumber = lastCertifiedReport.reportNumber
                if (overview.lastCertifiedReportIdWhenCreation != lastCertifiedReportId) {
                    overview.changedLastCertifiedReportEndDate = lastCertifiedOverview.endDate
                }
            }
        }
        return overview
    }
}
