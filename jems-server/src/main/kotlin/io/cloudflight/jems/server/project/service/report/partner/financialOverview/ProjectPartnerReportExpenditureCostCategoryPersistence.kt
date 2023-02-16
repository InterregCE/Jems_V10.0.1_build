package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryCurrentlyReportedWithParked
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryCurrentlyReportedWithReIncluded
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryPreviouslyReportedWithParked
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory

interface ProjectPartnerReportExpenditureCostCategoryPersistence {

    fun getCostCategories(partnerId: Long, reportId: Long): ReportExpenditureCostCategory

    fun getCostCategoriesCumulative(reportIds: Set<Long>): ExpenditureCostCategoryPreviouslyReportedWithParked

    fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReportedWithReIncluded: ExpenditureCostCategoryCurrentlyReportedWithReIncluded
    )

    fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControlWithParked: ExpenditureCostCategoryCurrentlyReportedWithParked,
    )

}
