package io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.budget.BudgetCostEntriesValidator
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.StaffCostType
import io.cloudflight.jems.server.project.service.partner.model.StaffCostUnitType
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

internal class UpdateBudgetStaffCostsTest : UnitTest() {

    private val partnerId = 1L
    private val listBudgetEntriesIds = listOf(1L, 2L)
    private val budgetStaffCostEntries = listBudgetEntriesIds
        .map { BudgetStaffCostEntry(it, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, StaffCostUnitType.HOUR, StaffCostType.UNIT_COST, emptySet(), emptySet()) }
        .let { it.plus(BudgetStaffCostEntry(null, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, StaffCostUnitType.HOUR, StaffCostType.UNIT_COST, emptySet(), emptySet())) }

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @MockK
    lateinit var budgetCostEntriesValidator: BudgetCostEntriesValidator

    @InjectMockKs
    lateinit var updateBudgetStaffCosts: UpdateBudgetStaffCosts

    @Test
    fun `should update and return budget staff cost entries for the specified partner when there isn't any validation error`() {

        every { budgetCostEntriesValidator.validate(budgetStaffCostEntries) } returns Unit
        every { persistence.deleteAllBudgetStaffCostsExceptFor(partnerId, listBudgetEntriesIds) } returns Unit
        every { persistence.createOrUpdateBudgetStaffCosts(partnerId, budgetStaffCostEntries) } returns budgetStaffCostEntries

        val result = updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)

        verify(atLeast = 1) { budgetCostEntriesValidator.validate(budgetStaffCostEntries) }
        verify(atLeast = 1) { persistence.deleteAllBudgetStaffCostsExceptFor(partnerId, listBudgetEntriesIds) }
        verify(atLeast = 1) { persistence.createOrUpdateBudgetStaffCosts(partnerId, budgetStaffCostEntries) }
        confirmVerified(persistence)

        assertEquals(budgetStaffCostEntries, result)
    }

    @Test
    fun `should throw I18nValidationException when there is a validation error`() {

        every { budgetCostEntriesValidator.validate(budgetStaffCostEntries) } throws I18nValidationException()

        assertThrows<I18nValidationException> {
            updateBudgetStaffCosts.updateBudgetStaffCosts(partnerId, budgetStaffCostEntries)
        }

        verify(atLeast = 1) { budgetCostEntriesValidator.validate(budgetStaffCostEntries) }
        confirmVerified(persistence)
    }

}
