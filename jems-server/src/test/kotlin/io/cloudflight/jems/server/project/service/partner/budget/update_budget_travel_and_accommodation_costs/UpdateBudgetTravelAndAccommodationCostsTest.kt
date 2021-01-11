package io.cloudflight.jems.server.project.service.partner.budget.update_budget_travel_and_accommodation_costs

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostEntriesValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

internal class UpdateBudgetTravelAndAccommodationCostsTest : UnitTest() {

    private val partnerId = 1L
    private val listBudgetEntriesIds = listOf(1L, 2L)
    private val budgetTravelAndAccommodationCostEntries =
        listBudgetEntriesIds
            .map { BudgetTravelAndAccommodationCostEntry(it, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, emptySet(), emptySet()) }
            .let { it.plus(BudgetTravelAndAccommodationCostEntry(null, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, emptySet(), emptySet())) }

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @MockK
    lateinit var budgetCostEntriesValidator: BudgetCostEntriesValidator

    @InjectMockKs
    lateinit var updateBudgetTravelAndAccommodationCosts: UpdateBudgetTravelAndAccommodationCosts

    @Test
    fun `should update and return budget travel and accommodation cost entries for the specified partner when there isn't any validation error`() {

        every { budgetCostEntriesValidator.validate(budgetTravelAndAccommodationCostEntries) } returns Unit
        every { persistence.deleteAllBudgetTravelAndAccommodationCostsExceptFor(partnerId, listBudgetEntriesIds) } returns Unit
        every { persistence.createOrUpdateBudgetTravelAndAccommodationCosts(partnerId, budgetTravelAndAccommodationCostEntries) } returns budgetTravelAndAccommodationCostEntries

        val result = updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(partnerId, budgetTravelAndAccommodationCostEntries)

        verify(atLeast = 1) { budgetCostEntriesValidator.validate(budgetTravelAndAccommodationCostEntries) }
        verify(atLeast = 1) { persistence.deleteAllBudgetTravelAndAccommodationCostsExceptFor(partnerId, listBudgetEntriesIds) }
        verify(atLeast = 1) { persistence.createOrUpdateBudgetTravelAndAccommodationCosts(partnerId, budgetTravelAndAccommodationCostEntries) }
        confirmVerified(persistence)

        assertEquals(budgetTravelAndAccommodationCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error`() {

        every { budgetCostEntriesValidator.validate(budgetTravelAndAccommodationCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetTravelAndAccommodationCosts.updateBudgetTravelAndAccommodationCosts(partnerId, budgetTravelAndAccommodationCostEntries)
        }

        verify(atLeast = 1) { budgetCostEntriesValidator.validate(budgetTravelAndAccommodationCostEntries) }
        confirmVerified(persistence)
    }

}
