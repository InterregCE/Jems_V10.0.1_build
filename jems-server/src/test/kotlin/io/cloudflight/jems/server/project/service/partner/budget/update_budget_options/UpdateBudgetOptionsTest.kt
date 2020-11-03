package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.server.project.service.partner.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetServiceTest.Companion.PARTNER_ID
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class UpdateBudgetOptionsTest {

    @MockK
    lateinit var persistence: ProjectBudgetPersistence

    @InjectMockKs
    lateinit var updateBudgetOptions: UpdateBudgetOptions

    @Test
    fun `should update budget options for the specified project and partner without any error when ProjectPartner exists`() {
        every { persistence.updateBudgetOptions(PARTNER_ID, 15, 8) } returns Unit
        every { persistence.deleteStaffCosts(PARTNER_ID) } returns Unit

        updateBudgetOptions.updateBudgetOptions(PARTNER_ID, 15, 8)

        verify(exactly = 1) { persistence.updateBudgetOptions(PARTNER_ID, 15, 8) }
        verify(exactly = 1) { persistence.deleteStaffCosts(PARTNER_ID) }
        confirmVerified(persistence)
    }

    @Test
    fun `should remove budget option when officeAdministrationFlatRate and staffCostsFlatRate are null`() {
        val officeAdministrationFlatRate = null
        val staffCostsFlatRate = null
        every { persistence.deleteBudgetOptions(PARTNER_ID) } returns Unit

        updateBudgetOptions.updateBudgetOptions(PARTNER_ID, officeAdministrationFlatRate, staffCostsFlatRate)

        verify(exactly = 1) { persistence.deleteBudgetOptions(PARTNER_ID) }
        confirmVerified(persistence)
    }

}
