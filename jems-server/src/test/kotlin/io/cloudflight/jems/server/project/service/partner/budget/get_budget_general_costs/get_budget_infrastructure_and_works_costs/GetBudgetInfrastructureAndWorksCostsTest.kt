package io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs.get_budget_infrastructure_and_works_costs

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetBudgetInfrastructureAndWorksCostsTest : UnitTest() {

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @InjectMockKs
    lateinit var getBudgetInfrastructureAndWorksCosts: GetBudgetInfrastructureAndWorksCosts

    @Test
    fun `should return budget infrastructure and works cost entries for the specified partner`() {
        val partnerId = 1L
        val budgetGeneralCostEntries = listOf(
            BudgetGeneralCostEntry(1, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 0L, emptySet(), emptySet(), emptySet()),
            BudgetGeneralCostEntry(2, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 0L, emptySet(), emptySet(), emptySet())
        )
        every { persistence.getBudgetInfrastructureAndWorksCosts(partnerId) } returns budgetGeneralCostEntries

        val result = getBudgetInfrastructureAndWorksCosts.getBudgetGeneralCosts(partnerId)

        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCosts(partnerId) }
        confirmVerified(persistence)

        assertEquals(budgetGeneralCostEntries, result)
    }
}
