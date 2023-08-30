package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.ProjectReportFinancialOverviewPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.getPartnerReportFinancialData.GetPartnerReportFinancialData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportFinancingSourceBreakdownCalculator(
    private val projectReportPersistence: ProjectReportPersistence,
    private val projectReportFinancialOverviewPersistence: ProjectReportFinancialOverviewPersistence,
    private val projectReportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence,
    private val projectReportVerificationExpenditurePersistence: ProjectReportVerificationExpenditurePersistence,
    private val getPartnerReportFinancialData: GetPartnerReportFinancialData,
) {

    @Transactional(readOnly = true)
    fun getFinancingSource(projectId: Long, reportId: Long): FinancingSourceBreakdown {
        val report = projectReportPersistence.getReportById(projectId = projectId, reportId = reportId)
        return if (report.status.isFinalized())
            projectReportFinalized(projectId, reportId)
        else
            projectReportInVerification(reportId)
    }

    fun projectReportFinalized(projectId: Long, reportId: Long): FinancingSourceBreakdown {
        val sources = projectReportFinancialOverviewPersistence.getOverviewPerFund(reportId)
        val projectReportAvailableFunds = projectReportCertificateCoFinancingPersistence.getAvailableFunds(reportId)
        val total = projectReportCertificateCoFinancingPersistence
            .getCoFinancing(projectId = projectId, reportId).currentVerified
            .toTotalLine(availableFunds = projectReportAvailableFunds)
        return FinancingSourceBreakdown(sources, total)
    }

    fun projectReportInVerification(reportId: Long): FinancingSourceBreakdown {
        val projectReportAvailableFunds = projectReportCertificateCoFinancingPersistence.getAvailableFunds(reportId)
        val sources = calculateSourcesAndSplits(
            verification = projectReportVerificationExpenditurePersistence.getProjectReportExpenditureVerification(reportId),
            availableFunds = projectReportAvailableFunds,
            partnerReportFinancialDataResolver = { getPartnerReportFinancialData.retrievePartnerReportFinancialData(it) },
        )
        val total = sources.sumUp(availableFunds = projectReportAvailableFunds)
        return FinancingSourceBreakdown(sources, total)
    }
}
