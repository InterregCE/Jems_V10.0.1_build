package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
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
    private val partnerReportCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence,
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
        val sourcesRegular = projectReportFinancialOverviewPersistence.getOverviewPerFund(reportId)
        val sourcesSpf = projectReportFinancialOverviewPersistence.getOverviewSpfPerFund(reportId)

        val sources = sourcesRegular.withSpf(sourcesSpf)

        val allAvailableFunds = sources.flatMap { source -> source.fundsSorted.map { it.first } }
            .distinctBy { it.id }
        val total = projectReportCertificateCoFinancingPersistence
            .getCoFinancing(projectId = projectId, reportId).currentVerified
            .toTotalLine(availableFunds = allAvailableFunds)
        return FinancingSourceBreakdown(sources, total)
    }

    fun projectReportInVerification(reportId: Long): FinancingSourceBreakdown {
        val sourcesRegular = calculateSourcesAndSplits(
            verification = projectReportVerificationExpenditurePersistence.getProjectReportExpenditureVerification(reportId),
            availableFundsResolver = { certificateId -> partnerReportCoFinancingPersistence.getAvailableFunds(certificateId) },
            partnerReportFinancialDataResolver = { getPartnerReportFinancialData.retrievePartnerReportFinancialData(it) },
        )
        val sourcesSpf = reportSpfClaimPersistence.getCurrentSpfContributionSplit(reportId)
        sourcesSpf?.fillInAdditionalSplitsForSpf()

        val sources = sourcesRegular.withSpf(sourcesSpf)

        val total = sources.sumUp()
        return FinancingSourceBreakdown(sources, total)
    }

    private fun List<FinancingSourceBreakdownLine>.withSpf(spf: FinancingSourceBreakdownLine?) =
        if (spf == null)
            this
        else
            this.plus(spf)

}
