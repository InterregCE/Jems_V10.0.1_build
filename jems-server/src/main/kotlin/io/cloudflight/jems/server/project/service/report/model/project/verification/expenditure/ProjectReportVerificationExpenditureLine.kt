package io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure

import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCostWithCategory
import java.math.BigDecimal
import java.time.ZonedDateTime

data class ProjectReportVerificationExpenditureLine(
    val expenditure: ProjectPartnerReportExpenditureItem,
    val partOfVerificationSample: Boolean,
    val deductedByJs: BigDecimal,
    val deductedByMa: BigDecimal,
    val amountAfterVerification: BigDecimal,
    val typologyOfErrorId: Long?,
    val parked: Boolean,
    val verificationComment: String?,
    val parkedOn: ZonedDateTime?
) : ExpenditureCostWithCategory {

    override fun getCategory(): BudgetCostCategory =
        when {
            expenditure.lumpSum != null -> BudgetCostCategory.LumpSum
            else -> expenditure.costCategory.translateCostCategory()
        }

}
