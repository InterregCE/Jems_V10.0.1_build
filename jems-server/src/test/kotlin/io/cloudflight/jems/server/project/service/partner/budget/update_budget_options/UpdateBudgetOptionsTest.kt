package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OfficeOnStaff
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.StaffCost
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.TravelOnStaff
import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerGeneralBudgetServiceTest.Companion.PARTNER_ID
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class UpdateBudgetOptionsTest {

    companion object {
        private fun notAdjustableRate(type: FlatRateType, rate: Int) = ProjectCallFlatRate(
            type = type,
            rate = rate,
            isAdjustable = false
        )
        private fun adjustableRate(type: FlatRateType, rate: Int) = ProjectCallFlatRate(
            type = type,
            rate = rate,
            isAdjustable = true
        )
    }

    @MockK
    lateinit var persistence: ProjectPartnerBudgetOptionsPersistence

    @MockK
    lateinit var callFlatRateSetupPersistence: CallFlatRateSetupPersistence

    @InjectMockKs
    lateinit var updateBudgetOptions: UpdateBudgetOptions

    @Test
    fun `should update budget options for the specified project and partner without any error when ProjectPartner exists`() {
        val flatRates = ProjectPartnerBudgetOptions(
            partnerId = PARTNER_ID,
            officeAndAdministrationFlatRate = 15,
            staffCostsFlatRate = 8,
        )
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns
            setOf(
                notAdjustableRate(type = OfficeOnStaff, rate = 15),
                notAdjustableRate(type = StaffCost, rate = 8)
            )

        every { persistence.updateBudgetOptions(PARTNER_ID, flatRates) } returns Unit
        every { persistence.deleteStaffCosts(PARTNER_ID) } returns Unit

        updateBudgetOptions.updateBudgetOptions(PARTNER_ID, flatRates)

        verify(exactly = 1) { persistence.updateBudgetOptions(PARTNER_ID, flatRates) }
        verify(exactly = 1) { persistence.deleteStaffCosts(PARTNER_ID) }
        confirmVerified(persistence)
    }

    @Test
    fun `should remove budget option when all flatRates are null`() {
        val flatRates = ProjectPartnerBudgetOptions(
            partnerId = PARTNER_ID,
            officeAndAdministrationFlatRate = null,
            travelAndAccommodationFlatRate = null,
            staffCostsFlatRate = null,
        )
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns setOf()
        every { persistence.deleteBudgetOptions(PARTNER_ID) } returns Unit

        updateBudgetOptions.updateBudgetOptions(PARTNER_ID, flatRates)

        verify(exactly = 1) { persistence.deleteBudgetOptions(PARTNER_ID) }
        confirmVerified(persistence)
    }

    @Test
    fun `invalid rate value - OfficeOnStuff not adjustable`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns
            // this one is not adjustable
            setOf(notAdjustableRate(type = OfficeOnStaff, rate = 15))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(PARTNER_ID, ProjectPartnerBudgetOptions(partnerId = PARTNER_ID, officeAndAdministrationFlatRate = 12))
        }
        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!!["OfficeOnStaff"])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.not.adjustable"))
    }

    @Test
    fun `invalid rate value - OfficeOnStuff is exceeding limit`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns
            // max is set to 10
            setOf(adjustableRate(type = OfficeOnStaff, rate = 10))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(PARTNER_ID, ProjectPartnerBudgetOptions(partnerId = PARTNER_ID, officeAndAdministrationFlatRate = 12))
        }
        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!!["OfficeOnStaff"])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.exceeded"))
    }

    @Test
    fun `invalid rate type - staffCostFlatRate and travelFlatRate not available`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns
            // this one will not be used (but would be possible)
            setOf(notAdjustableRate(type = OfficeOnStaff, rate = 15))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(PARTNER_ID, ProjectPartnerBudgetOptions(
                partnerId = PARTNER_ID,
                staffCostsFlatRate = 10,
                travelAndAccommodationFlatRate = 10,
            ))
        }
        assertThat(ex.i18nFieldErrors).hasSize(2)
        assertThat(ex.i18nFieldErrors!![StaffCost.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.type.not.allowed"))
        assertThat(ex.i18nFieldErrors!![TravelOnStaff.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.type.not.allowed"))
    }

    @Test
    fun `should remove budget data if flat rates are set instead of them`() {
        every { persistence.deleteStaffCosts(any()) } answers {}
        every { persistence.deleteTravelAndAccommodationCosts(any()) } answers {}
        val slotOptions = slot<ProjectPartnerBudgetOptions>()
        every { persistence.updateBudgetOptions(PARTNER_ID, capture(slotOptions)) } answers {}
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(PARTNER_ID) } returns setOf(
            adjustableRate(type = StaffCost, rate = 10),
            adjustableRate(type = TravelOnStaff, rate = 10),
        )

        val options = ProjectPartnerBudgetOptions(
            partnerId = PARTNER_ID,
            travelAndAccommodationFlatRate = 5,
            staffCostsFlatRate = 5,
        )
        updateBudgetOptions.updateBudgetOptions(PARTNER_ID, options)

        verify(exactly = 1) { persistence.deleteTravelAndAccommodationCosts(PARTNER_ID) }
        verify(exactly = 1) { persistence.deleteStaffCosts(PARTNER_ID) }

        assertThat(slotOptions.captured).isEqualTo(options)
    }

}
