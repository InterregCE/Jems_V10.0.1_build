package io.cloudflight.jems.server.project.controller.report.financialOverview

import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureLumpSumBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureInvestmentBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureUnitCostBreakdownDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportFinancialOverviewApi
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.GetReportExpenditureCoFinancingBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown.GetReportExpenditureLumpSumBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.GetReportExpenditureInvestmentsBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown.GetReportExpenditureUnitCostBreakdownInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportFinancialOverviewController(
    private val getReportExpenditureCoFinancingBreakdown: GetReportExpenditureCoFinancingBreakdownInteractor,
    private val getReportExpenditureCostCategoryBreakdown: GetReportExpenditureCostCategoryBreakdownInteractor,
    private val getReportExpenditureLumpSumBreakdown: GetReportExpenditureLumpSumBreakdownInteractor,
    private val getReportExpenditureInvestmentsBreakdown: GetReportExpenditureInvestmentsBreakdownInteractor,
    private val getReportExpenditureUnitCostBreakdown: GetReportExpenditureUnitCostBreakdownInteractor,
) : ProjectPartnerReportFinancialOverviewApi {

    override fun getCoFinancingBreakdown(partnerId: Long, reportId: Long): ExpenditureCoFinancingBreakdownDTO =
        getReportExpenditureCoFinancingBreakdown.get(partnerId = partnerId, reportId = reportId).toDto()

    override fun getCostCategoriesBreakdown(partnerId: Long, reportId: Long): ExpenditureCostCategoryBreakdownDTO =
        getReportExpenditureCostCategoryBreakdown.get(partnerId = partnerId, reportId = reportId).toDto()

    override fun getInvestmentsBreakdown(partnerId: Long, reportId: Long): ExpenditureInvestmentBreakdownDTO =
        getReportExpenditureInvestmentsBreakdown.get(partnerId = partnerId, reportId = reportId).toDto()

    override fun getLumpSumBreakdown(partnerId: Long, reportId: Long): ExpenditureLumpSumBreakdownDTO =
        getReportExpenditureLumpSumBreakdown.get(partnerId = partnerId, reportId = reportId).toDto()

    override fun getUnitCostBreakdown(partnerId: Long, reportId: Long): ExpenditureUnitCostBreakdownDTO =
        getReportExpenditureUnitCostBreakdown.get(partnerId = partnerId, reportId = reportId).toDto()

}
