package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_infrastructure_and_works_costs

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.UpdateBudgetGeneralCostsTest
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdTateBudgetInfrastructureAndWorksCostsTest : UpdateBudgetGeneralCostsTest() {

    @InjectMockKs
    lateinit var updateBudgetInfrastructureAndWorksCosts: UpdateBudgetInfrastructureAndWorksCosts

    @Test
    fun `should update and return budget infrastructure and works cost entries for the specified partner when there isn't any validation error`() {

        every { budgetCostEntriesValidator.validate(budgetGeneralCostEntries) } returns Unit
        every { persistence.deleteAllBudgetInfrastructureAndWorksCostsExceptFor(partnerId, listBudgetEntriesIds) } returns Unit
        every { persistence.createOrUpdateBudgetInfrastructureAndWorksCosts(partnerId, budgetGeneralCostEntries) } returns budgetGeneralCostEntries

        val result = updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(partnerId, budgetGeneralCostEntries)

        verify(atLeast = 1) { budgetCostEntriesValidator.validate(budgetGeneralCostEntries) }
        verify(atLeast = 1) { persistence.deleteAllBudgetInfrastructureAndWorksCostsExceptFor(partnerId, listBudgetEntriesIds) }
        verify(atLeast = 1) { persistence.createOrUpdateBudgetInfrastructureAndWorksCosts(partnerId, budgetGeneralCostEntries) }
        confirmVerified(persistence)

        assertEquals(budgetGeneralCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error`() {

        every { budgetCostEntriesValidator.validate(budgetGeneralCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetInfrastructureAndWorksCosts.updateBudgetGeneralCosts(partnerId, budgetGeneralCostEntries)
        }

        verify(atLeast = 1) { budgetCostEntriesValidator.validate(budgetGeneralCostEntries) }
        confirmVerified(persistence)
    }

}
