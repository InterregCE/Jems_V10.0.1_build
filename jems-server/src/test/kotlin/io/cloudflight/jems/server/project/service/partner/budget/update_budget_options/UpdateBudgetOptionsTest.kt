package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerGeneralBudgetServiceTest.Companion.PARTNER_ID
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class UpdateBudgetOptionsTest {

    companion object {
        private const val CALL_ID: Long = 1
        private fun notAdjustableRate(type: FlatRateType, rate: Int) = ProjectCallFlatRate(
            callId = CALL_ID,
            type = type,
            rate = rate,
            isAdjustable = false
        )
        private fun adjustableRate(type: FlatRateType, rate: Int) = ProjectCallFlatRate(
            callId = CALL_ID,
            type = type,
            rate = rate,
            isAdjustable = true
        )
    }

    @MockK
    lateinit var persistence: ProjectPartnerBudgetPersistence

    @MockK
    lateinit var callFlatRateSetupPersistence: CallFlatRateSetupPersistence

    @InjectMockKs
    lateinit var updateBudgetOptions: UpdateBudgetOptions

    @Test
    fun `should update budget options for the specified project and partner without any error when ProjectPartner exists`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns
            setOf(
                notAdjustableRate(type = FlatRateType.OfficeOnStaff, rate = 15),
                notAdjustableRate(type = FlatRateType.StaffCost, rate = 8)
            )

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
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns setOf()
        every { persistence.deleteBudgetOptions(PARTNER_ID) } returns Unit

        updateBudgetOptions.updateBudgetOptions(PARTNER_ID, officeAdministrationFlatRate, staffCostsFlatRate)

        verify(exactly = 1) { persistence.deleteBudgetOptions(PARTNER_ID) }
        confirmVerified(persistence)
    }

    @Test
    fun `invalid rate value - OfficeOnStuff not adjustable`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns
            // this one is not adjustable
            setOf(notAdjustableRate(type = FlatRateType.OfficeOnStaff, rate = 15))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(PARTNER_ID, 12, null)
        }
        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!!["OfficeOnStaff"])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.not.adjustable"))
    }

    @Test
    fun `invalid rate value - OfficeOnStuff is exceeding limit`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns
            // max is set to 10
            setOf(adjustableRate(type = FlatRateType.OfficeOnStaff, rate = 10))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(PARTNER_ID, 12, null)
        }
        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!!["OfficeOnStaff"])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.exceeded"))
    }

    @Test
    fun `invalid rate type - staffCost not available`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns
            // this one will not be used (but would be possible)
            setOf(notAdjustableRate(type = FlatRateType.OfficeOnStaff, rate = 15))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(PARTNER_ID, null, 10)
        }
        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!!["StaffCost"])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.type.not.allowed"))
    }

}
