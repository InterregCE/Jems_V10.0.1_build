package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import java.math.BigDecimal

interface ExpenditureCost : ExpenditureCostAfterSubmission {
    val id: Long?
    var lumpSumId: Long?
    val costCategory: ReportBudgetCategory
    override var declaredAmountAfterSubmission: BigDecimal?
    val parkingMetadata: ExpenditureParkingMetadata?

    override fun getCategory(): BudgetCostCategory =
        when {
            lumpSumId != null -> BudgetCostCategory.LumpSum
            else -> costCategory.translateCostCategory()
        }

}
