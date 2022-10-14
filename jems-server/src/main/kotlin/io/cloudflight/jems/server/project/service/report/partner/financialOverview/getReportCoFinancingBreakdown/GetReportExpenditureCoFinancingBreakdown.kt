package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.extractOverview
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportExpenditureCoFinancingBreakdown(
    private val reportPersistence: ProjectReportPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectReportExpenditureCoFinancingPersistence,
    private val reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService,
    private val reportContributionPersistence: ProjectReportContributionPersistence,
) : GetReportExpenditureCoFinancingBreakdownInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportExpenditureCoFinancingBreakdownException::class)
    override fun get(partnerId: Long, reportId: Long): ExpenditureCoFinancingBreakdown {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId)

        val data = reportExpenditureCoFinancingPersistence.getCoFinancing(partnerId = partnerId, reportId = reportId)
        val coFinancing = data.toLinesModel()

        if (!report.status.isClosed()) {
            val expenditureTotal = reportExpenditureCostCategoryCalculatorService
                .getSubmittedOrCalculateCurrent(partnerId = partnerId, reportId = reportId).total
            val contributions = reportContributionPersistence
                .getPartnerReportContribution(partnerId, reportId = reportId).extractOverview()

            val currentValues = getCurrentFrom(
                contributions.generateCoFinCalculationInputData(
                    totalEligibleBudget = expenditureTotal.totalEligibleBudget,
                    currentValueToSplit = expenditureTotal.currentReport,
                    funds = report.identification.coFinancing,
                )
            )

            coFinancing.fillInCurrent(current = currentValues)
        }

        return coFinancing.fillInOverviewFields()
    }

}
