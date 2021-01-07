package io.cloudflight.jems.server.project.service.partner.budget.get_budget_general_costs.get_budget_external_expertise_and_services

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetBudgetExternalExpertiseAndServicesCostsTest : UnitTest() {

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @InjectMockKs
    lateinit var getBudgetExternalExpertiseAndServicesCosts: GetBudgetExternalExpertiseAndServicesCosts

    @Test
    fun `should return budget external expertise and services cost entries for the specified partner`() {
        val partnerId = 1L
        val budgetGeneralCostEntries = listOf(
            BudgetGeneralCostEntry(1, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 0L, emptySet(), emptySet(), emptySet()),
            BudgetGeneralCostEntry(2, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 0L, emptySet(), emptySet(), emptySet())
        )
        every { persistence.getBudgetExternalExpertiseAndServicesCosts(partnerId) } returns budgetGeneralCostEntries

        val result = getBudgetExternalExpertiseAndServicesCosts.getBudgetGeneralCosts(partnerId)

        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCosts(partnerId) }
        confirmVerified(persistence)

        assertEquals(budgetGeneralCostEntries, result)
    }
}
