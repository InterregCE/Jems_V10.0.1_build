package io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.percentage
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
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
    private val staffCostTotal = BigDecimal.valueOf(1_324_500.00)
    private val travelAndAccommodationCostTotal = BigDecimal.valueOf(1_160_040.00)
    private val equipmentCostTotal = BigDecimal.valueOf(321)
    private val externalExpertiseAndServicesCostTotal = BigDecimal.valueOf(662.25)
    private val infrastructureAndWorksCostTotal = BigDecimal.valueOf(773.36)

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @MockK
    lateinit var getBudgetOptionsInteractor: GetBudgetOptionsInteractor

    @InjectMockKs
    lateinit var getBudgetTotalCost: GetBudgetTotalCost

    @BeforeAll
    fun setup() {

        every { persistence.getBudgetStaffCostTotal(partnerId) } returns staffCostTotal
        every { persistence.getBudgetEquipmentCostTotal(partnerId) } returns equipmentCostTotal
        every { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) } returns externalExpertiseAndServicesCostTotal
        every { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) } returns infrastructureAndWorksCostTotal
        every { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) } returns travelAndAccommodationCostTotal
    }

    @Test
    fun `should return sum of budget cost entries for the specified partner when budgetOptions is null `() {

        every { getBudgetOptionsInteractor.getBudgetOptions(partnerId) } returns null

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verify(atLeast = 1) { getBudgetOptionsInteractor.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetStaffCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetEquipmentCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) }
        confirmVerified(persistence)

        assertEquals(sumOf(staffCostTotal, travelAndAccommodationCostTotal, equipmentCostTotal, externalExpertiseAndServicesCostTotal, infrastructureAndWorksCostTotal), result)
    }

    @Test
    fun `should return sum of budget cost entries for the specified partner when no flat rate is set in the budgetOptions`() {
        val budgetOptions = newBudgetOptionsInstance(partnerId = partnerId)

        every { getBudgetOptionsInteractor.getBudgetOptions(partnerId) } returns budgetOptions

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verify(atLeast = 1) { getBudgetOptionsInteractor.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetStaffCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetEquipmentCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) }
        confirmVerified(persistence)

        assertEquals(sumOf(staffCostTotal, travelAndAccommodationCostTotal, equipmentCostTotal, externalExpertiseAndServicesCostTotal, infrastructureAndWorksCostTotal), result)
    }

    @Test
    fun `should return sum of budget cost entries and flat rate costs for the specified partner when travelAndAccommodationOnStaffCostsFlatRate is set in the budgetOptions`() {

        val budgetOptions = newBudgetOptionsInstance(partnerId = partnerId, travelAndAccommodationOnStaffCostsFlatRate = 10)
        val travelAndAccommodationCostTotal = staffCostTotal.percentage(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate!!)
        every { getBudgetOptionsInteractor.getBudgetOptions(partnerId) } returns budgetOptions

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verify(atLeast = 1) { getBudgetOptionsInteractor.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetStaffCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetEquipmentCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) }
        confirmVerified(persistence)

        assertEquals(sumOf(travelAndAccommodationCostTotal, externalExpertiseAndServicesCostTotal, equipmentCostTotal, infrastructureAndWorksCostTotal, staffCostTotal), result)
    }

    @Test
    fun `should return sum of budget cost entries and flat rate costs for the specified partner when staffCostsFlatRate is set in the budgetOptions`() {

        val budgetOptions = newBudgetOptionsInstance(partnerId = partnerId, staffCostsFlatRate = 15)
        val staffCostTotal = sumOf(travelAndAccommodationCostTotal, externalExpertiseAndServicesCostTotal, equipmentCostTotal, infrastructureAndWorksCostTotal).percentage(budgetOptions.staffCostsFlatRate!!)
        every { getBudgetOptionsInteractor.getBudgetOptions(partnerId) } returns budgetOptions

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verify(atLeast = 1) { getBudgetOptionsInteractor.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetEquipmentCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) }
        confirmVerified(persistence)

        assertEquals(sumOf(travelAndAccommodationCostTotal, externalExpertiseAndServicesCostTotal, equipmentCostTotal, infrastructureAndWorksCostTotal, staffCostTotal), result)
    }

    @Test
    fun `should return sum of budget cost entries and flat rate costs for the specified partner when staffCostsFlatRate and travelAndAccommodationOnStaffCostsFlatRate are set in the budgetOptions`() {

        val budgetOptions = newBudgetOptionsInstance(partnerId = partnerId, travelAndAccommodationOnStaffCostsFlatRate = 10, staffCostsFlatRate = 20)
        val staffCostTotal = sumOf(externalExpertiseAndServicesCostTotal, equipmentCostTotal, infrastructureAndWorksCostTotal).percentage(budgetOptions.staffCostsFlatRate!!)
        val travelAndAccommodationCostTotal = staffCostTotal.percentage(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate!!)
        every { getBudgetOptionsInteractor.getBudgetOptions(partnerId) } returns budgetOptions

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verify(atLeast = 1) { getBudgetOptionsInteractor.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetEquipmentCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) }
        confirmVerified(persistence)

        assertEquals(sumOf(staffCostTotal, travelAndAccommodationCostTotal, externalExpertiseAndServicesCostTotal, equipmentCostTotal, infrastructureAndWorksCostTotal), result)
    }

    @Test
    fun `should return sum of budget cost entries and flat rate costs when officeAndAdministrationOnStaffCostsFlatRate is set in the budgetOptions`() {

        val budgetOptions = newBudgetOptionsInstance(partnerId = partnerId, officeAndAdministrationOnStaffCostsFlatRate = 10)
        val officeAndAdministrationOnStaffCostTotal = staffCostTotal.percentage(budgetOptions.officeAndAdministrationOnStaffCostsFlatRate!!)
        every { getBudgetOptionsInteractor.getBudgetOptions(partnerId) } returns budgetOptions

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verify(atLeast = 1) { getBudgetOptionsInteractor.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetStaffCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetEquipmentCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) }
        confirmVerified(persistence)

        assertEquals(sumOf(officeAndAdministrationOnStaffCostTotal, travelAndAccommodationCostTotal, externalExpertiseAndServicesCostTotal, equipmentCostTotal, infrastructureAndWorksCostTotal, staffCostTotal), result)
    }

    @Test
    fun `should return sum of budget cost entries and flat rate costs for the specified partner when otherCostsOnStaffCostsFlatRate is set in the budgetOptions`() {

        val budgetOptions = newBudgetOptionsInstance(partnerId = partnerId, otherCostsOnStaffCostsFlatRate = 30)
        val otherCostsOnStaffCostTotal = staffCostTotal.percentage(budgetOptions.otherCostsOnStaffCostsFlatRate!!)
        every { getBudgetOptionsInteractor.getBudgetOptions(partnerId) } returns budgetOptions

        val result = getBudgetTotalCost.getBudgetTotalCost(partnerId)

        verify(atLeast = 1) { getBudgetOptionsInteractor.getBudgetOptions(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetStaffCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetTravelAndAccommodationCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetEquipmentCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId) }
        verify(atLeast = 1) { persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId) }
        confirmVerified(persistence)

        assertEquals(sumOf(otherCostsOnStaffCostTotal, travelAndAccommodationCostTotal, externalExpertiseAndServicesCostTotal, equipmentCostTotal, infrastructureAndWorksCostTotal, staffCostTotal), result)
    }

    private fun newBudgetOptionsInstance(partnerId: Long = 1L, officeAndAdministrationOnStaffCostsFlatRate: Int? = null, travelAndAccommodationOnStaffCostsFlatRate: Int? = null, staffCostsFlatRate: Int? = null, otherCostsOnStaffCostsFlatRate: Int? = null) =
        ProjectPartnerBudgetOptions(partnerId, officeAndAdministrationOnStaffCostsFlatRate, travelAndAccommodationOnStaffCostsFlatRate, staffCostsFlatRate, otherCostsOnStaffCostsFlatRate)

    private fun sumOf(vararg values: BigDecimal) = values.reduce { acc, value -> acc.plus(value) }
}
