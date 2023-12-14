package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportCertificateCoFinancingBreakdownCalculator(
    private val reportPersistence: ProjectReportPersistence,
    private val reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val certificateCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence,
) {

    @Transactional(readOnly = true)
    fun get(projectId: Long, reportId: Long): CertificateCoFinancingBreakdown {
        val report = reportPersistence.getReportById(projectId = projectId, reportId).status

        val data = reportCertificateCoFinancingPersistence.getCoFinancing(projectId = projectId, reportId = reportId)
        val coFinancing = data.toLinesModel()

        if (report.isOpenForNumbersChanges()) {
            val certificateIds = reportCertificatePersistence.listCertificatesOfProjectReport(reportId).mapTo(HashSet()) { it.id }

            val certificateCurrentValues = certificateCoFinancingPersistence.getCoFinancingTotalEligible(certificateIds)
            val spfContributionCurrentValues = reportSpfClaimPersistence.getCurrentSpfContribution(reportId)
            val currentValues = certificateCurrentValues.plus(spfContributionCurrentValues)

            coFinancing.fillInCurrent(current = currentValues)
        }
        return coFinancing.fillInOverviewFields()
    }

}
