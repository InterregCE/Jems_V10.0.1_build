package io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.percentage
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetBudgetTotalCostTest : UnitTest() {

    val partnerId = 1L
    private val staffCostTotal = 1_324_500.0.toScaledBigDecimal()
    private val travelCostTotal = 1_160_040.toScaledBigDecimal()
    private val equipmentCostTotal = 321.toScaledBigDecimal()
    private val externalCostTotal = 662.25.toScaledBigDecimal()
    private val infrastructureCostTotal = 773.36.toScaledBigDecimal()
    private val unitCostTotal = 563.36.toScaledBigDecimal()
    private val lumpSumsTotal = 123.4.toScaledBigDecimal()

    @MockK
    lateinit var persistence: ProjectPartnerBudgetCostsPersistence

    @MockK
    lateinit var getBudgetOptions: GetBudgetOptionsInteractor

    @MockK
    lateinit var budgetCostsCalculatorService: BudgetCostsCalculatorService

    @InjectMockKs
    lateinit var getBudgetTotalCost: GetBudgetTotalCost

    @BeforeAll
    fun setup() {
        every { persistence.getBudgetStaffCostTotal(partnerId) } returns staffCostTotal
        every { persistence.getBudgetEquipmentCostTotal(partnerId) } returns equipmentCostTotal
        every { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) } returns externalCostTotal
        every { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) } returns infrastructureCostTotal
        every { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) } returns travelCostTotal
        every { persistence.getBudgetUnitCostTotal(partnerId) } returns unitCostTotal
        every { persistence.getBudgetLumpSumsCostTotal(partnerId) } returns lumpSumsTotal
    }

    @Test
    fun `should return sum of budget cost entries for the specified partner when budgetOptions is null `() {
        val expectedTotalCost = expectedTotalCost()
        every { getBudgetOptions.getBudgetOptions(partnerId) } returns null
        every {
            budgetCostsCalculatorService.calculateCosts(
                null,
                unitCostTotal,
                lumpSumsTotal,
                externalCostTotal,
                equipmentCostTotal,
                infrastructureCostTotal,
                travelCostTotal,
                staffCostTotal
            )
        } returns BudgetCostsCalculationResult(
            staffCostTotal, travelCostTotal, BigDecimal.ZERO, BigDecimal.ZERO, expectedTotalCost
        )

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verifyIfGetBudgetOptionsAndTotalCostsAreCalled()
        verifyIfCalculateCostsIsCalled(null)
        verify(atLeast = 1) { persistence.getBudgetStaffCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) }
        confirmVerified(persistence, budgetCostsCalculatorService)

        assertEquals(expectedTotalCost, result)
    }

    @Test
    fun `should return sum of budget cost entries for the specified partner when no flat rate is set in the budgetOptions`() {
        val budgetOptions = newBudgetOptionsInstance(partnerId = partnerId)
        val expectedTotalCost = expectedTotalCost()
        every { getBudgetOptions.getBudgetOptions(partnerId) } returns budgetOptions
        every {
            budgetCostsCalculatorService.calculateCosts(
                budgetOptions,
                unitCostTotal,
                lumpSumsTotal,
                externalCostTotal,
                equipmentCostTotal,
                infrastructureCostTotal,
                travelCostTotal,
                staffCostTotal
            )
        } returns BudgetCostsCalculationResult(
            staffCostTotal, travelCostTotal, BigDecimal.ZERO, BigDecimal.ZERO, expectedTotalCost
        )

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verifyIfGetBudgetOptionsAndTotalCostsAreCalled()
        verifyIfCalculateCostsIsCalled(budgetOptions)
        verify(atLeast = 1) { persistence.getBudgetStaffCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) }
        confirmVerified(persistence, budgetCostsCalculatorService)

        assertEquals(expectedTotalCost, result)
    }

    @Test
    fun `should return sum of budget cost entries and flat rate costs for the specified partner when travelAndAccommodationOnStaffCostsFlatRate is set in the budgetOptions`() {

        val budgetOptions =
            newBudgetOptionsInstance(partnerId = partnerId, travelAndAccommodationOnStaffCostsFlatRate = 10)
        val expectedTravelCostTotal =
            staffCostTotal.percentage(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate!!)
        val expectedTotalCost = expectedTotalCost(travelCosts = expectedTravelCostTotal)
        every { getBudgetOptions.getBudgetOptions(partnerId) } returns budgetOptions
        every {
            budgetCostsCalculatorService.calculateCosts(
                budgetOptions,
                unitCostTotal,
                lumpSumsTotal,
                externalCostTotal,
                equipmentCostTotal,
                infrastructureCostTotal,
                BigDecimal.ZERO,
                staffCostTotal
            )
        } returns BudgetCostsCalculationResult(
            staffCostTotal, expectedTravelCostTotal, BigDecimal.ZERO, BigDecimal.ZERO, expectedTotalCost
        )

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verifyIfGetBudgetOptionsAndTotalCostsAreCalled()
        verifyIfCalculateCostsIsCalled(budgetOptions, travelCosts = BigDecimal.ZERO)
        verify(atLeast = 1) { persistence.getBudgetStaffCostTotal(partnerId) }
        confirmVerified(persistence, budgetCostsCalculatorService)

        assertEquals(expectedTotalCost, result)
    }

    @Test
    fun `should return sum of budget cost entries and flat rate costs for the specified partner when staffCostsFlatRate is set in the budgetOptions`() {

        val budgetOptions = newBudgetOptionsInstance(partnerId = partnerId, staffCostsFlatRate = 15)
        val expectedStaffCostTotal = sumOf(
            travelCostTotal,
            externalCostTotal,
            equipmentCostTotal,
            infrastructureCostTotal
        ).percentage(budgetOptions.staffCostsFlatRate!!)
        val expectedTotalCost = expectedTotalCost(staffCosts = expectedStaffCostTotal)
        every { getBudgetOptions.getBudgetOptions(partnerId) } returns budgetOptions

        every {
            budgetCostsCalculatorService.calculateCosts(
                budgetOptions,
                unitCostTotal,
                lumpSumsTotal,
                externalCostTotal,
                equipmentCostTotal,
                infrastructureCostTotal,
                travelCostTotal,
                BigDecimal.ZERO
            )
        } returns BudgetCostsCalculationResult(
            expectedStaffCostTotal, travelCostTotal, BigDecimal.ZERO, BigDecimal.ZERO, expectedTotalCost
        )

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verifyIfGetBudgetOptionsAndTotalCostsAreCalled()
        verifyIfCalculateCostsIsCalled(budgetOptions, staffCosts = BigDecimal.ZERO)
        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) }
        confirmVerified(persistence, budgetCostsCalculatorService)

        assertEquals(expectedTotalCost, result)
    }

    @Test
    fun `should return sum of budget cost entries and flat rate costs for the specified partner when staffCostsFlatRate and travelAndAccommodationOnStaffCostsFlatRate are set in the budgetOptions`() {

        val budgetOptions = newBudgetOptionsInstance(
            partnerId = partnerId,
            travelAndAccommodationOnStaffCostsFlatRate = 10,
            staffCostsFlatRate = 20
        )
        val expectedStaffCostTotal = sumOf(
            externalCostTotal,
            equipmentCostTotal,
            infrastructureCostTotal
        ).percentage(budgetOptions.staffCostsFlatRate!!)
        val expectedTravelCostTotal =
            expectedStaffCostTotal.percentage(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate!!)
        val expectedTotalCost =
            expectedTotalCost(travelCosts = expectedTravelCostTotal, staffCosts = expectedStaffCostTotal)
        every { getBudgetOptions.getBudgetOptions(partnerId) } returns budgetOptions

        every {
            budgetCostsCalculatorService.calculateCosts(
                budgetOptions,
                unitCostTotal,
                lumpSumsTotal,
                externalCostTotal,
                equipmentCostTotal,
                infrastructureCostTotal,
                BigDecimal.ZERO,
                BigDecimal.ZERO
            )
        } returns BudgetCostsCalculationResult(
            expectedStaffCostTotal, expectedTravelCostTotal, BigDecimal.ZERO, BigDecimal.ZERO, expectedTotalCost
        )

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verifyIfGetBudgetOptionsAndTotalCostsAreCalled()
        verifyIfCalculateCostsIsCalled(budgetOptions, staffCosts = BigDecimal.ZERO, travelCosts = BigDecimal.ZERO)
        confirmVerified(persistence, budgetCostsCalculatorService)

        assertEquals(expectedTotalCost, result)
    }

    private fun newBudgetOptionsInstance(
        partnerId: Long = 1L,
        officeAndAdministrationOnStaffCostsFlatRate: Int? = null,
        officeAndAdministrationOnDirectCostsFlatRate: Int? = null,
        travelAndAccommodationOnStaffCostsFlatRate: Int? = null,
        staffCostsFlatRate: Int? = null,
        otherCostsOnStaffCostsFlatRate: Int? = null
    ) =
        ProjectPartnerBudgetOptions(
            partnerId,
            officeAndAdministrationOnStaffCostsFlatRate,
            officeAndAdministrationOnDirectCostsFlatRate,
            travelAndAccommodationOnStaffCostsFlatRate,
            staffCostsFlatRate,
            otherCostsOnStaffCostsFlatRate
        )

    private fun verifyIfGetBudgetOptionsAndTotalCostsAreCalled() {
        verify(atLeast = 1) { getBudgetOptions.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetEquipmentCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetUnitCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetLumpSumsCostTotal(partnerId) }
    }

    private fun verifyIfCalculateCostsIsCalled(
        budgetOptions: ProjectPartnerBudgetOptions?,
        staffCosts: BigDecimal = staffCostTotal,
        travelCosts: BigDecimal = travelCostTotal
    ) {
        verify(atLeast = 1) {
            budgetCostsCalculatorService.calculateCosts(
                budgetOptions,
                unitCostTotal,
                lumpSumsTotal,
                externalCostTotal,
                equipmentCostTotal,
                infrastructureCostTotal,
                travelCosts,
                staffCosts
            )
        }
    }

    private fun expectedTotalCost(
        staffCosts: BigDecimal = staffCostTotal,
        travelCosts: BigDecimal = travelCostTotal,
        equipmentCosts: BigDecimal = equipmentCostTotal,
        externalCosts: BigDecimal = externalCostTotal,
        infrastructureCosts: BigDecimal = infrastructureCostTotal,
        unitCosts: BigDecimal = unitCostTotal,
        lumpSumsCosts: BigDecimal = lumpSumsTotal,
        officeCosts: BigDecimal = BigDecimal.ZERO,
        otherCosts: BigDecimal = BigDecimal.ZERO,
    ) =
        sumOf(
            staffCosts,
            travelCosts,
            equipmentCosts,
            externalCosts,
            infrastructureCosts,
            officeCosts,
            otherCosts,
            unitCosts,
            lumpSumsCosts
        )

    private fun sumOf(vararg values: BigDecimal) = values.reduce { acc, value -> acc.plus(value) }
}
