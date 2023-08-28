package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.ProjectReportVerificationClarificationData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.ProjectReportVerificationConclusionData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.expenditure.ProjectPartnerReportExpenditureItemData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.expenditure.ProjectReportVerificationExpenditureLineData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.expenditure.ProjectReportVerificationRiskBasedData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.financialOverview.financingSource.FinancingSourceBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLineData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.financialOverview.workOverview.VerificationWorkOverviewData
import io.cloudflight.jems.plugin.contract.models.report.project.verification.financialOverview.workOverview.VerificationWorkOverviewLineData
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationRiskBased
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverviewLine
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import java.math.BigDecimal

private val mapper = Mappers.getMapper(ProjectReportVerificationDataProviderMapper::class.java)

fun List<ProjectReportVerificationClarification>.toClarificationDataModel() = map { mapper.map(it) }
fun ProjectReportVerificationConclusion.toDataModel() = mapper.map(this)
fun List<ProjectReportVerificationExpenditureLine>.toExpenditureDataModel() = map { mapper.map(it) }
fun ProjectReportVerificationRiskBased.toDataModel() = mapper.map(this)
fun FinancingSourceBreakdown.toDataModel() = mapper.map(this)
fun VerificationWorkOverview.toDataModel() = mapper.map(this)

@Mapper
interface ProjectReportVerificationDataProviderMapper {

    fun map(model: ProjectReportVerificationClarification): ProjectReportVerificationClarificationData
    fun map(model: ProjectReportVerificationConclusion): ProjectReportVerificationConclusionData

    fun map(model: ProjectReportVerificationExpenditureLine): ProjectReportVerificationExpenditureLineData
    fun map(model: ProjectPartnerReportExpenditureItem): ProjectPartnerReportExpenditureItemData

    fun map(model: ProjectReportVerificationRiskBased): ProjectReportVerificationRiskBasedData

    fun map(model: FinancingSourceBreakdown): FinancingSourceBreakdownData
    fun map(model: FinancingSourceBreakdownLine): FinancingSourceBreakdownLineData
    fun map(model: FinancingSourceBreakdownSplitLine): FinancingSourceBreakdownSplitLineData
    fun map(model: Pair<ProgrammeFund, BigDecimal>): Pair<ProgrammeFundData, BigDecimal>

    fun map(model: VerificationWorkOverview): VerificationWorkOverviewData
    fun map(model: VerificationWorkOverviewLine): VerificationWorkOverviewLineData
}
