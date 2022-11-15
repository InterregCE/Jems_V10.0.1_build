package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown

interface GetReportExpenditureCostCategoryBreakdownInteractor {

    fun get(partnerId: Long, reportId: Long): ExpenditureCostCategoryBreakdown

}
