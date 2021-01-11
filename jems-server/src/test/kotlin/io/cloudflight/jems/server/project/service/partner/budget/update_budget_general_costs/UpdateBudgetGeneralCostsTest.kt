package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostEntriesValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.mockk.impl.annotations.MockK
import java.math.BigDecimal

open class UpdateBudgetGeneralCostsTest : UnitTest() {

    protected val partnerId = 1L
    protected val listBudgetEntriesIds = listOf(1L, 2L)
    protected val budgetGeneralCostEntries = listBudgetEntriesIds
        .map { BudgetGeneralCostEntry(it, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, null, emptySet(), emptySet()) }
        .let { it.plus(BudgetGeneralCostEntry(null, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, null, emptySet(), emptySet())) }

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @MockK
    lateinit var budgetCostEntriesValidator: BudgetCostEntriesValidator

}
