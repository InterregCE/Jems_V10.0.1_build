package io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
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

internal class GetBudgetCostsTest : UnitTest() {

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @InjectMockKs
    lateinit var getBudgetCosts: GetBudgetCosts

    @Test
    fun `should return budget costs for the specified partner`() {
        val partnerId = 1L
        val budgetStaffCostEntries = budgetStaffCostEntries()
        val budgetTravelAndAccommodationCostEntries = budgetTravelAndAccommodationCostEntries()
        val budgetGeneralCostEntries = budgetGeneralCostEntries()

        every { persistence.getBudgetEquipmentCosts(partnerId) } returns budgetGeneralCostEntries
        every { persistence.getBudgetExternalExpertiseAndServicesCosts(partnerId) } returns budgetGeneralCostEntries
        every { persistence.getBudgetInfrastructureAndWorksCosts(partnerId) } returns budgetGeneralCostEntries
        every { persistence.getBudgetTravelAndAccommodationCosts(partnerId) } returns budgetTravelAndAccommodationCostEntries
        every { persistence.getBudgetStaffCosts(partnerId) } returns budgetStaffCostEntries

        val result = getBudgetCosts.getBudgetCosts(partnerId)

        verify(atLeast = 1) { persistence.getBudgetEquipmentCosts(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCosts(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCosts(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCosts(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetStaffCosts(partnerId) }
        confirmVerified(persistence)

        assertEquals(budgetStaffCostEntries, result.staffCosts)
        assertEquals(budgetTravelAndAccommodationCostEntries, result.travelCosts)
        assertEquals(budgetGeneralCostEntries, result.equipmentCosts)
        assertEquals(budgetGeneralCostEntries, result.externalCosts)
        assertEquals(budgetGeneralCostEntries, result.infrastructureCosts)
    }


    private fun budgetStaffCostEntries() = listOf(
        BudgetStaffCostEntry(1, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, StaffCostUnitType.HOUR, StaffCostType.UNIT_COST, emptySet(), emptySet()),
        BudgetStaffCostEntry(2, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, StaffCostUnitType.HOUR, StaffCostType.UNIT_COST, emptySet(), emptySet())
    )

    private fun budgetTravelAndAccommodationCostEntries() = listOf(
        BudgetTravelAndAccommodationCostEntry(1, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, emptySet(), emptySet()),
        BudgetTravelAndAccommodationCostEntry(2, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, emptySet(), emptySet())
    )

    private fun budgetGeneralCostEntries() = listOf(
        BudgetGeneralCostEntry(1, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 0L, emptySet(), emptySet(), emptySet()),
        BudgetGeneralCostEntry(2, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 0L, emptySet(), emptySet(), emptySet())
    )
}

