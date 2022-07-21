package io.cloudflight.jems.server.project.controller.report.financialOverview

import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportFinancialOverviewApi
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.GetReportExpenditureCoFinancingBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryBreakdownInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportFinancialOverviewController(
    private val getReportExpenditureCoFinancingBreakdown: GetReportExpenditureCoFinancingBreakdownInteractor,
    private val getReportExpenditureCostCategoryBreakdown: GetReportExpenditureCostCategoryBreakdownInteractor,
) : ProjectPartnerReportFinancialOverviewApi {

    override fun getCoFinancingBreakdown(partnerId: Long, reportId: Long): ExpenditureCoFinancingBreakdownDTO =
        getReportExpenditureCoFinancingBreakdown.get(partnerId = partnerId, reportId = reportId).toDto()

    override fun getCostCategoriesBreakdown(partnerId: Long, reportId: Long): ExpenditureCostCategoryBreakdownDTO =
        getReportExpenditureCostCategoryBreakdown.get(partnerId = partnerId, reportId = reportId).toDto()

}
