package io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.model.*
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
    lateinit var budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence

    @InjectMockKs
    lateinit var getBudgetCosts: GetBudgetCosts

    @Test
    fun `should return budget costs for the specified partner`() {
        val partnerId = 1L
        val budgetStaffCostEntries = budgetStaffCostEntries()
        val budgetTravelAndAccommodationCostEntries = budgetTravelAndAccommodationCostEntries()
        val budgetGeneralCostEntries = budgetGeneralCostEntries()
        val budgetUnitCostEntries = budgetUnitCostEntries()

        every { budgetCostsPersistence.getBudgetEquipmentCosts(partnerId) } returns budgetGeneralCostEntries
        every { budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerId) } returns budgetGeneralCostEntries
        every { budgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerId) } returns budgetGeneralCostEntries
        every { budgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerId) } returns budgetTravelAndAccommodationCostEntries
        every { budgetCostsPersistence.getBudgetStaffCosts(partnerId) } returns budgetStaffCostEntries
        every { budgetCostsPersistence.getBudgetUnitCosts(partnerId) } returns budgetUnitCostEntries

        val result = getBudgetCosts.getBudgetCosts(partnerId)

        verify(atLeast = 1) { budgetCostsPersistence.getBudgetEquipmentCosts(partnerId) }
        verify(atLeast = 1) { budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerId) }
        verify(atLeast = 1) { budgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerId) }
        verify(atLeast = 1) { budgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerId) }
        verify(atLeast = 1) { budgetCostsPersistence.getBudgetStaffCosts(partnerId) }
        verify(atLeast = 1) { budgetCostsPersistence.getBudgetUnitCosts(partnerId) }
        confirmVerified(budgetCostsPersistence)

        assertEquals(budgetStaffCostEntries, result.staffCosts)
        assertEquals(budgetTravelAndAccommodationCostEntries, result.travelCosts)
        assertEquals(budgetGeneralCostEntries, result.equipmentCosts)
        assertEquals(budgetGeneralCostEntries, result.externalCosts)
        assertEquals(budgetGeneralCostEntries, result.infrastructureCosts)
        assertEquals(budgetUnitCostEntries, result.unitCosts)
    }


    private fun budgetStaffCostEntries() = listOf(
        BudgetStaffCostEntry(
            1,
            BigDecimal.ONE,
            BigDecimal.ONE,
            mutableSetOf(),
            BigDecimal.ONE,
            emptySet(),
            emptySet(),
            emptySet(),
            null
        ),
        BudgetStaffCostEntry(
            2,
            BigDecimal.ONE,
            BigDecimal.ONE,
            mutableSetOf(),
            BigDecimal.ONE,
            emptySet(),
            emptySet(),
            emptySet(),
            1
        )
    )

    private fun budgetTravelAndAccommodationCostEntries() = listOf(
        BudgetTravelAndAccommodationCostEntry(
            1,
            BigDecimal.ONE,
            BigDecimal.ONE,
            mutableSetOf(),
            BigDecimal.ONE,
            emptySet(),
            emptySet()
        ),
        BudgetTravelAndAccommodationCostEntry(
            2,
            BigDecimal.ONE,
            BigDecimal.ONE,
            mutableSetOf(),
            BigDecimal.ONE,
            emptySet(),
            emptySet(),
            2
        )
    )

    private fun budgetGeneralCostEntries() = listOf(
        BudgetGeneralCostEntry(
            1,
            BigDecimal.ONE,
            BigDecimal.ONE,
            mutableSetOf(),
            BigDecimal.ONE,
            0L,
            emptySet(),
            emptySet(),
            emptySet()
        ),
        BudgetGeneralCostEntry(
            2,
            BigDecimal.ONE,
            BigDecimal.ONE,
            mutableSetOf(),
            BigDecimal.ONE,
            0L,
            emptySet(),
            emptySet(),
            emptySet(),
            3
        )
    )

    private fun budgetUnitCostEntries() = listOf(
        BudgetUnitCostEntry(1, BigDecimal.ONE, mutableSetOf(), BigDecimal.ONE, 1)
    )

}
