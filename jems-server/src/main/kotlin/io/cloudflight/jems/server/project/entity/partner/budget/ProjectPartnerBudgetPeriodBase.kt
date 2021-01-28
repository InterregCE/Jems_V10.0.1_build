package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal

interface ProjectPartnerBudgetPeriodBase<T : ProjectPartnerBudgetBase> {
    val budgetPeriodId: BudgetPeriodId<T>
    val amount: BigDecimal
}
