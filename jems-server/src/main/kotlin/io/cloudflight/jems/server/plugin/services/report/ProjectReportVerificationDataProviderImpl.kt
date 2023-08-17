package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.report.project.verification.ProjectReportVerificationClarificationData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.ProjectReportVerificationConclusionData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.expenditure.ProjectReportVerificationRiskBasedData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.financialOverview.financingSource.FinancingSourceBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.financialOverview.workOverview.VerificationWorkOverviewData
import io.cloudflight.jems.plugin.contract.services.report.ProjectReportVerificationDataProvider
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.GetProjectReportFinancingSourceBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview.GetProjectReportVerificationWorkOverviewCalculator
import org.springframework.stereotype.Service

@Service
class ProjectReportVerificationDataProviderImpl(
    private val verificationPersistence: ProjectReportVerificationPersistence,
    private val expenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence,
    private val getFinancingSourceBreakdownCalculator: GetProjectReportFinancingSourceBreakdownCalculator,
    private val getProjectReportVerificationWorkOverviewCalculator: GetProjectReportVerificationWorkOverviewCalculator,
) : ProjectReportVerificationDataProvider {

    override fun getClarifications(projectId: Long, reportId: Long): List<ProjectReportVerificationClarificationData> =
        verificationPersistence.getVerificationClarifications(reportId = reportId)
            .toClarificationDataModel()

    override fun getConclusion(projectId: Long, reportId: Long): ProjectReportVerificationConclusionData =
        verificationPersistence.getVerificationConclusion(projectId = projectId, reportId = reportId)
            .toDataModel()

    override fun getExpenditureVerification(projectId: Long, reportId: Long): List<ProjectReportVerificationExpenditureLineData> =
        expenditureVerificationPersistence.getProjectReportExpenditureVerification(projectReportId = reportId)
            .toExpenditureDataModel()

    override fun getExpenditureVerificationRiskBased(projectId: Long, reportId: Long): ProjectReportVerificationRiskBasedData =
        expenditureVerificationPersistence.getExpenditureVerificationRiskBasedData(projectId = projectId, projectReportId = reportId)
            .toDataModel()

    override fun getFinancingSourceBreakdown(projectId: Long, reportId: Long): FinancingSourceBreakdownData =
        getFinancingSourceBreakdownCalculator.getFinancingSource(projectId = projectId, reportId = reportId)
            .toDataModel()

    override fun getVerificationWorkOverview(projectId: Long, reportId: Long): VerificationWorkOverviewData =
        getProjectReportVerificationWorkOverviewCalculator.getWorkOverviewPerPartner(reportId = reportId)
            .toDataModel()

}
