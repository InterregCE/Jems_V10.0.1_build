package io.cloudflight.jems.server.project.service.partner.budget.get_budget_options

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test


internal class GetBudgetOptionsTest : UnitTest() {

    private val partnerId = 1L
    private val budgetOptions = ProjectPartnerBudgetOptions(
        partnerId,
        15,
        20,
        12,
        10
    )

    @MockK
    lateinit var optionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @InjectMockKs
    lateinit var getBudgetOptions: GetBudgetOptions


    @Test
    fun `should return budget options for the specified partner`() {
        every { optionsPersistence.getBudgetOptions(partnerId) } returns budgetOptions

        val returnedBudgetOptions = getBudgetOptions.getBudgetOptions(partnerId)

        verify(atLeast = 1) { optionsPersistence.getBudgetOptions(partnerId) }
        confirmVerified(optionsPersistence)

        assertEquals(partnerId, returnedBudgetOptions?.partnerId)
        assertEquals(budgetOptions.officeAndAdministrationOnStaffCostsFlatRate, returnedBudgetOptions?.officeAndAdministrationOnStaffCostsFlatRate)
        assertEquals(budgetOptions.staffCostsFlatRate, returnedBudgetOptions?.staffCostsFlatRate)
        assertEquals(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate, returnedBudgetOptions?.travelAndAccommodationOnStaffCostsFlatRate)
        assertEquals(budgetOptions.otherCostsOnStaffCostsFlatRate, returnedBudgetOptions?.otherCostsOnStaffCostsFlatRate)
    }

    @Test
    fun `should return null when budget options for partner not exists`() {
        every { optionsPersistence.getBudgetOptions(partnerId) } returns null

        val returnedBudgetOptions = getBudgetOptions.getBudgetOptions(partnerId)

        verify(atLeast = 1) { optionsPersistence.getBudgetOptions(partnerId) }
        confirmVerified(optionsPersistence)

        assertNull(returnedBudgetOptions)
    }

}
