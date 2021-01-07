package io.cloudflight.jems.server.project.service.partner.budget.get_budget_staff_costs

import io.cloudflight.jems.server.UnitTest
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
import java.math.BigDecimal

internal class GetBudgetStaffCostsTest : UnitTest() {

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @InjectMockKs
    lateinit var getBudgetStaffCosts: GetBudgetStaffCosts

    @Test
    fun `should return budget staff cost entries for the specified partner`() {
        val partnerId = 1L
        val budgetStaffCostEntries = listOf(
            BudgetStaffCostEntry(1, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, StaffCostUnitType.HOUR, StaffCostType.UNIT_COST, emptySet(), emptySet()),
            BudgetStaffCostEntry(2, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, StaffCostUnitType.HOUR, StaffCostType.UNIT_COST, emptySet(), emptySet())
        )
        every { persistence.getBudgetStaffCosts(partnerId) } returns budgetStaffCostEntries

        val result = getBudgetStaffCosts.getBudgetStaffCosts(partnerId)

        verify(atLeast = 1) { persistence.getBudgetStaffCosts(partnerId) }
        confirmVerified(persistence)

        assertEquals(budgetStaffCostEntries, result)
    }
}
