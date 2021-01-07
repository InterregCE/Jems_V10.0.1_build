package io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.update_budget_equipment_costs

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.budget.update_budget_general_costs.UpdateBudgetGeneralCostsTest
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateBudgetEquipmentCostsTest : UpdateBudgetGeneralCostsTest() {

    @InjectMockKs
    lateinit var updateBudgetEquipmentCosts: UpdateBudgetEquipmentCosts

    @Test
    fun `should update and return budget equipment cost entries for the specified partner when there isn't any validation error`() {

        every { budgetCostEntriesValidator.validate(budgetGeneralCostEntries) } returns Unit
        every { persistence.deleteAllBudgetEquipmentCostsExceptFor(partnerId, listBudgetEntriesIds) } returns Unit
        every { persistence.createOrUpdateBudgetEquipmentCosts(partnerId, budgetGeneralCostEntries) } returns budgetGeneralCostEntries

        val result = updateBudgetEquipmentCosts.updateBudgetGeneralCosts(partnerId, budgetGeneralCostEntries)

        verify(atLeast = 1) { budgetCostEntriesValidator.validate(budgetGeneralCostEntries) }
        verify(atLeast = 1) { persistence.deleteAllBudgetEquipmentCostsExceptFor(partnerId, listBudgetEntriesIds) }
        verify(atLeast = 1) { persistence.createOrUpdateBudgetEquipmentCosts(partnerId, budgetGeneralCostEntries) }
        confirmVerified(persistence)

        assertEquals(budgetGeneralCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error`() {

        every { budgetCostEntriesValidator.validate(budgetGeneralCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetEquipmentCosts.updateBudgetGeneralCosts(partnerId, budgetGeneralCostEntries)
        }

        verify(atLeast = 1) { budgetCostEntriesValidator.validate(budgetGeneralCostEntries) }
        confirmVerified(persistence)
    }

}
