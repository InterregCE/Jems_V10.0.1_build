package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.extractOverview
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportExpenditureCoFinancingBreakdownCalculator(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService,
    private val reportContributionPersistence: ProjectPartnerReportContributionPersistence,
) {

    companion object {
        fun split(
            toSplit: BigDecimal,
            contributions: ProjectPartnerReportContributionOverview,
            funds: List<ProjectPartnerCoFinancing>,
            total: BigDecimal,
        ): ReportExpenditureCoFinancingColumn =
            getCurrentFrom(
                contributions.generateCoFinCalculationInputData(
                    totalEligibleBudget = total,
                    currentValueToSplit = toSplit,
                    funds = funds,
                )
            ).toColumn()
    }

    @Transactional(readOnly = true)
    fun get(partnerId: Long, reportId: Long): ExpenditureCoFinancingBreakdown {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId)

        val data = reportExpenditureCoFinancingPersistence.getCoFinancing(partnerId = partnerId, reportId = reportId)
        val coFinancing = data.toLinesModel()

        if (report.status.isOpenForNumbersChanges()) {
            val expenditureCurrent = reportExpenditureCostCategoryCalculatorService
                .getSubmittedOrCalculateCurrent(partnerId = partnerId, reportId = reportId)
            val totalWithoutSpf = expenditureCurrent.total.totalEligibleBudget.minus(expenditureCurrent.spfCost.totalEligibleBudget)

            val contributions = reportContributionPersistence
                .getPartnerReportContribution(partnerId, reportId = reportId).extractOverview()

            val currentValues = ExpenditureCoFinancingCurrentWithReIncluded(
                current = split(
                    toSplit = expenditureCurrent.total.currentReport,
                    contributions = contributions,
                    funds = report.identification.coFinancing,
                    total = totalWithoutSpf,
                ),
                currentReIncluded = split(
                    toSplit = expenditureCurrent.total.currentReportReIncluded,
                    contributions = contributions,
                    funds = report.identification.coFinancing,
                    total = totalWithoutSpf,
                ),
            )

            coFinancing.fillInCurrent(current = currentValues.current)
            coFinancing.fillInCurrentReIncluded(currentReIncluded = currentValues.currentReIncluded)
        }

        return coFinancing.fillInOverviewFields()
    }

}
