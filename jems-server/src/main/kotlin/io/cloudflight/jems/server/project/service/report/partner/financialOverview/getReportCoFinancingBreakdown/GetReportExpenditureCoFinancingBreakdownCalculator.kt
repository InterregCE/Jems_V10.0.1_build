package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
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

    @Transactional(readOnly = true)
    fun get(partnerId: Long, reportId: Long): ExpenditureCoFinancingBreakdown {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId)

        val data = reportExpenditureCoFinancingPersistence.getCoFinancing(partnerId = partnerId, reportId = reportId)
        val coFinancing = data.toLinesModel()

        if (report.status.isOpen()) {
            val expenditureTotal = reportExpenditureCostCategoryCalculatorService
                .getSubmittedOrCalculateCurrent(partnerId = partnerId, reportId = reportId).total
            val contributions = reportContributionPersistence
                .getPartnerReportContribution(partnerId, reportId = reportId).extractOverview()

            val currentValues = ExpenditureCoFinancingCurrentWithReIncluded(
                current = split(
                    toSplit = expenditureTotal.currentReport,
                    contributions, funds = report.identification.coFinancing,
                    total = expenditureTotal.totalEligibleBudget,
                ),
                currentReIncluded = split(
                    toSplit = expenditureTotal.currentReportReIncluded,
                    contributions, funds = report.identification.coFinancing,
                    total = expenditureTotal.totalEligibleBudget,
                ),
            )

            coFinancing.fillInCurrent(current = currentValues.current)
            coFinancing.fillInCurrentReIncluded(currentReIncluded = currentValues.currentReIncluded)
        }

        return coFinancing.fillInOverviewFields()
    }

    private fun split(
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
        )

}
