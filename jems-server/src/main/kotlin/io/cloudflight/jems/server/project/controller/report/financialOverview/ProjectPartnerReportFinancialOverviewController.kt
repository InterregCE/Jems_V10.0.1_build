package io.cloudflight.jems.server.project.controller.report.financialOverview

import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportFinancialOverviewApi
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryBreakdown
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportFinancialOverviewController(
    private val getReportExpenditureCostCategoryBreakdown: GetReportExpenditureCostCategoryBreakdown,
) : ProjectPartnerReportFinancialOverviewApi {

    override fun getCostCategoriesBreakdown(partnerId: Long, reportId: Long): ExpenditureCostCategoryBreakdownDTO =
        getReportExpenditureCostCategoryBreakdown.get(partnerId = partnerId, reportId = reportId).toDto()

}
