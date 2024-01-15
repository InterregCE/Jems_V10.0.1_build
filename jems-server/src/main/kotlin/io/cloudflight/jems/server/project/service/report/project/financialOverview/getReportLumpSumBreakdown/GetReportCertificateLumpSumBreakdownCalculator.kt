package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.fillInOverviewFields
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportCertificateLumpSumBreakdownCalculator(
    private val reportPersistence: ProjectReportPersistence,
    private val reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val reportExpenditureLumpSumPersistence: ProjectPartnerReportLumpSumPersistence
) {

    /**
     * This fun will either
     *  - just fetch reported certificate lump sum breakdown for submitted report
     *  - or calculate currently reported values from actual certificate lump sum
     */
    @Transactional(readOnly = true)
    fun getSubmittedOrCalculateCurrent(projectId: Long, reportId: Long): CertificateLumpSumBreakdown {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId).status

        val data = reportCertificateLumpSumPersistence.getLumpSums(projectId = projectId, reportId = reportId)

        if (report.isOpenForNumbersChanges()) {
            val certificates = reportCertificatePersistence.listCertificatesOfProjectReport(reportId)
            val currentLumpSums = reportExpenditureLumpSumPersistence.getLumpSumCumulativeAfterControl(certificates.map {it.id}.toSet())
            data.fillInCurrent(current = currentLumpSums)
        }
        val lumpSumLines = data.fillInOverviewFields()

        return CertificateLumpSumBreakdown(
            lumpSums = lumpSumLines,
            total = lumpSumLines.sumUp().fillInOverviewFields(),
        )
    }

}
