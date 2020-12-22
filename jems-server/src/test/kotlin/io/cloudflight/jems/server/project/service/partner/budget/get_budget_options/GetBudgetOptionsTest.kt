package io.cloudflight.jems.server.project.service.partner.budget.get_budget_options

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerGeneralBudgetServiceTest.Companion.PARTNER_ID
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test


internal class GetBudgetOptionsTest : UnitTest() {

    private val budgetOptions = ProjectPartnerBudgetOptions(
        PARTNER_ID,
        15,
        20,
        12,
        10
    )

    @MockK
    lateinit var persistence: ProjectPartnerBudgetOptionsPersistence

    @InjectMockKs
    lateinit var getBudgetOptions: GetBudgetOptions


    @Test
    fun `should return budget options for the specified partner`() {
        every { persistence.getBudgetOptions(PARTNER_ID) } returns budgetOptions

        val returnedBudgetOptions = getBudgetOptions.getBudgetOptions(PARTNER_ID)

        verify(atLeast = 1) { persistence.getBudgetOptions(PARTNER_ID) }
        confirmVerified(persistence)

        assertEquals(PARTNER_ID, returnedBudgetOptions?.partnerId)
        assertEquals(budgetOptions.officeAndAdministrationOnStaffCostsFlatRate, returnedBudgetOptions?.officeAndAdministrationOnStaffCostsFlatRate)
        assertEquals(budgetOptions.staffCostsFlatRate, returnedBudgetOptions?.staffCostsFlatRate)
        assertEquals(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate, returnedBudgetOptions?.travelAndAccommodationOnStaffCostsFlatRate)
        assertEquals(budgetOptions.otherCostsOnStaffCostsFlatRate, returnedBudgetOptions?.otherCostsOnStaffCostsFlatRate)
    }

    @Test
    fun `should return null when budget options for partner not exists`() {
        every { persistence.getBudgetOptions(PARTNER_ID) } returns null

        val returnedBudgetOptions = getBudgetOptions.getBudgetOptions(PARTNER_ID)

        verify(atLeast = 1) { persistence.getBudgetOptions(PARTNER_ID) }
        confirmVerified(persistence)

        assertNull(returnedBudgetOptions)
    }

}
