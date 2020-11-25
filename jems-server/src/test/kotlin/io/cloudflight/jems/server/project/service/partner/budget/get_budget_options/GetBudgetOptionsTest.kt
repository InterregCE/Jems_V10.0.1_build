package io.cloudflight.jems.server.project.service.partner.budget.get_budget_options

import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerGeneralBudgetServiceTest.Companion.PARTNER_ID
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerGeneralBudgetServiceTest.Companion.budgetOptions
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class GetBudgetOptionsTest {
    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @InjectMockKs
    lateinit var getBudgetOptions: GetBudgetOptions


    @Test
    fun `should return budget options for the specified partner`() {
        every { persistence.getBudgetOptions(PARTNER_ID) } returns budgetOptions

        val returnedBudgetOptions = getBudgetOptions.getBudgetOptions(PARTNER_ID)

        verify(exactly = 1) { persistence.getBudgetOptions(PARTNER_ID) }
        confirmVerified(persistence)

        assertEquals(PARTNER_ID, returnedBudgetOptions?.partnerId)
        assertEquals(budgetOptions.officeAdministrationFlatRate, returnedBudgetOptions?.officeAdministrationFlatRate)
        assertEquals(budgetOptions.staffCostsFlatRate, returnedBudgetOptions?.staffCostsFlatRate)
    }

    @Test
    fun `should return null when budget options for partner not exists`() {
        every { persistence.getBudgetOptions(PARTNER_ID) } returns null

        val returnedBudgetOptions = getBudgetOptions.getBudgetOptions(PARTNER_ID)

        verify(exactly = 1) { persistence.getBudgetOptions(PARTNER_ID) }
        confirmVerified(persistence)

        assertNull(returnedBudgetOptions)
    }

}
