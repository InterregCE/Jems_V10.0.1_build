package io.cloudflight.jems.server.project.service.partner.budget.get_budget_travel_and_accommodation_costs

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

internal class GetBudgetTravelAndAccommodationCostsTest  : UnitTest() {

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @InjectMockKs
    lateinit var getBudgetTravelAndAccommodationCosts: GetBudgetTravelAndAccommodationCosts

    @Test
    fun `should return budget travel and accommodation cost entries for the specified partner`() {
        val partnerId = 1L
        val budgetTravelAndAccommodationCostEntries = listOf(
            BudgetTravelAndAccommodationCostEntry(1, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,   emptySet(), emptySet()),
            BudgetTravelAndAccommodationCostEntry(2, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,   emptySet(), emptySet())
        )
        every { persistence.getBudgetTravelAndAccommodationCosts(partnerId) } returns budgetTravelAndAccommodationCostEntries

        val result = getBudgetTravelAndAccommodationCosts.getBudgetTravelAndAccommodationCosts(partnerId)

        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCosts(partnerId) }
        confirmVerified(persistence)

        assertEquals(budgetTravelAndAccommodationCostEntries, result)
    }
}
