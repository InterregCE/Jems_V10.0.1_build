package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OTHER_COSTS_ON_STAFF_COSTS
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.STAFF_COSTS
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateBaseBudgetPropertiesOptionsTest : UnitTest() {

    private val partnerId = 1L

    @MockK
    lateinit var optionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @MockK
    lateinit var callFlatRateSetupPersistence: CallFlatRateSetupPersistence

    @InjectMockKs
    lateinit var updateBudgetOptions: UpdateBudgetOptions

    @Test
    fun `should update budget options for the specified project and partner without any error when ProjectPartner exists`() {
        val flatRates = ProjectPartnerBudgetOptions(
            partnerId = partnerId,
            officeAndAdministrationOnStaffCostsFlatRate = 15,
            staffCostsFlatRate = 8,
        )
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns
            setOf(
                notAdjustableRate(type = OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS, rate = 15),
                notAdjustableRate(type = STAFF_COSTS, rate = 8)
            )

        every { optionsPersistence.updateBudgetOptions(partnerId, flatRates) } returns Unit
        every { optionsPersistence.deleteStaffCosts(partnerId) } returns Unit

        updateBudgetOptions.updateBudgetOptions(partnerId, flatRates)

        verify(atLeast = 1) { optionsPersistence.updateBudgetOptions(partnerId, flatRates) }
        verify(atLeast = 1) { optionsPersistence.deleteStaffCosts(partnerId) }
        verify(atLeast = 1) { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) }
    }

    @Test
    fun `should remove budget option when all flatRates are null`() {
        val flatRates = ProjectPartnerBudgetOptions(
            partnerId = partnerId,
            officeAndAdministrationOnStaffCostsFlatRate = null,
            travelAndAccommodationOnStaffCostsFlatRate = null,
            staffCostsFlatRate = null,
        )
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns setOf()
        every { optionsPersistence.deleteBudgetOptions(partnerId) } returns Unit

        updateBudgetOptions.updateBudgetOptions(partnerId, flatRates)

        verify(atLeast = 1) { optionsPersistence.deleteBudgetOptions(partnerId) }
        verify(atLeast = 1) { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) }
    }


    @Test
    fun `should throw Exception when otherCostsOnStaffCostsFlatRate is not adjustable and it's value does not match the configured value in the call flat rate setup`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns
            setOf(notAdjustableRate(type = OTHER_COSTS_ON_STAFF_COSTS, rate = 15))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(partnerId, ProjectPartnerBudgetOptions(partnerId = partnerId, otherCostsOnStaffCostsFlatRate = 12))
        }

        verify(atLeast = 1) { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) }
        confirmVerified(callFlatRateSetupPersistence)

        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!![OTHER_COSTS_ON_STAFF_COSTS.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.not.adjustable"))
    }

    @Test
    fun `should throw Exception when otherCostsOnStaffCostsFlatRate's exceeds the configured value in the call flat rate setup`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns
            setOf(adjustableRate(type = OTHER_COSTS_ON_STAFF_COSTS, rate = 10))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(partnerId, ProjectPartnerBudgetOptions(partnerId = partnerId, otherCostsOnStaffCostsFlatRate = 12))
        }

        verify(atLeast = 1) { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) }
        confirmVerified(callFlatRateSetupPersistence)

        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!![OTHER_COSTS_ON_STAFF_COSTS.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.exceeded"))

    }

    @Test
    fun `should throw Exception when otherCostsOnStaffCostsFlatRate option is combined with officeAndAdministrationOnStaffCostsFlatRate option`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns setOf(
            adjustableRate(OTHER_COSTS_ON_STAFF_COSTS, 10),
            adjustableRate(OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS, 20),
        )

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(partnerId, ProjectPartnerBudgetOptions(partnerId = partnerId, otherCostsOnStaffCostsFlatRate = 12, officeAndAdministrationOnStaffCostsFlatRate = 10))
        }

        verify(atLeast = 1) { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) }
        confirmVerified(callFlatRateSetupPersistence)

        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!![OTHER_COSTS_ON_STAFF_COSTS.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.combination.is.not.valid"))
    }

    @Test
    fun `should throw Exception when otherCostsOnStaffCostsFlatRate option is combined with travelAndAccommodationOnStaffCostsFlatRate option`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns setOf(
            adjustableRate(OTHER_COSTS_ON_STAFF_COSTS, 10),
            adjustableRate(TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS, 20),
        )

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(partnerId, ProjectPartnerBudgetOptions(partnerId = partnerId, otherCostsOnStaffCostsFlatRate = 12, travelAndAccommodationOnStaffCostsFlatRate = 10))
        }

        verify(atLeast = 1) { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) }
        confirmVerified(callFlatRateSetupPersistence)

        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!![OTHER_COSTS_ON_STAFF_COSTS.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.combination.is.not.valid"))
    }

    @Test
    fun `should throw Exception when otherCostsOnStaffCostsFlatRate option is combined with staffCostsFlatRate option`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns setOf(
            adjustableRate(OTHER_COSTS_ON_STAFF_COSTS, 10),
            adjustableRate(STAFF_COSTS, 20),
        )

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(partnerId, ProjectPartnerBudgetOptions(partnerId = partnerId, otherCostsOnStaffCostsFlatRate = 12, staffCostsFlatRate = 10))
        }

        verify(atLeast = 1) { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) }
        confirmVerified(callFlatRateSetupPersistence)

        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!![OTHER_COSTS_ON_STAFF_COSTS.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.combination.is.not.valid"))
    }


    @Test
    fun `invalid rate value - OfficeOnStuff not adjustable`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns
            // this one is not adjustable
            setOf(notAdjustableRate(type = OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS, rate = 15))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(partnerId, ProjectPartnerBudgetOptions(partnerId = partnerId, officeAndAdministrationOnStaffCostsFlatRate = 12))
        }
        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!![OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.not.adjustable"))
    }

    @Test
    fun `invalid rate value - OfficeOnStuff is exceeding limit`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns
            // max is set to 10
            setOf(adjustableRate(type = OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS, rate = 10))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(partnerId, ProjectPartnerBudgetOptions(partnerId = partnerId, officeAndAdministrationOnStaffCostsFlatRate = 12))
        }
        assertThat(ex.i18nFieldErrors).hasSize(1)
        assertThat(ex.i18nFieldErrors!![OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.exceeded"))
    }

    @Test
    fun `invalid rate type - staffCostFlatRate and travelFlatRate not available`() {
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns
            // this one will not be used (but would be possible)
            setOf(notAdjustableRate(type = OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS, rate = 15))

        val ex = assertThrows<I18nValidationException> {
            updateBudgetOptions.updateBudgetOptions(partnerId, ProjectPartnerBudgetOptions(
                partnerId = partnerId,
                staffCostsFlatRate = 10,
                travelAndAccommodationOnStaffCostsFlatRate = 10,
            ))
        }
        assertThat(ex.i18nFieldErrors).hasSize(2)
        assertThat(ex.i18nFieldErrors!![STAFF_COSTS.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.type.not.allowed"))
        assertThat(ex.i18nFieldErrors!![TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS.name])
            .isEqualTo(I18nFieldError("project.partner.budget.options.flatRate.type.not.allowed"))
    }

    @Test
    fun `should remove budget data if flat rates are set instead of them`() {
        every { optionsPersistence.deleteStaffCosts(any()) } answers {}
        every { optionsPersistence.deleteTravelAndAccommodationCosts(any()) } answers {}
        val slotOptions = slot<ProjectPartnerBudgetOptions>()
        every { optionsPersistence.updateBudgetOptions(partnerId, capture(slotOptions)) } answers {}
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns setOf(
            adjustableRate(type = STAFF_COSTS, rate = 10),
            adjustableRate(type = TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS, rate = 10),
        )

        val options = ProjectPartnerBudgetOptions(
            partnerId = partnerId,
            travelAndAccommodationOnStaffCostsFlatRate = 5,
            staffCostsFlatRate = 5,
        )
        updateBudgetOptions.updateBudgetOptions(partnerId, options)

        verify(atLeast = 1) { optionsPersistence.deleteTravelAndAccommodationCosts(partnerId) }
        verify(atLeast = 1) { optionsPersistence.deleteStaffCosts(partnerId) }
        verify(atLeast = 1) { optionsPersistence.updateBudgetOptions(partnerId, any()) }
        verify(atLeast = 1) { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) }
        confirmVerified(optionsPersistence, callFlatRateSetupPersistence)

        assertThat(slotOptions.captured).isEqualTo(options)
    }

    @Test
    fun `should remove TravelAndAccommodationCosts, EquipmentCosts, ExternalCosts, InfrastructureCosts and UnitCosts from the budget table if otherCostsOnStaffCostsFlatRate is set`() {
        every { optionsPersistence.deleteTravelAndAccommodationCosts(any()) } answers {}
        every { optionsPersistence.deleteEquipmentCosts(any()) } answers {}
        every { optionsPersistence.deleteExternalCosts(any()) } answers {}
        every { optionsPersistence.deleteInfrastructureCosts(any()) } answers {}
        every { optionsPersistence.deleteUnitCosts(any()) } answers {}
        val slotOptions = slot<ProjectPartnerBudgetOptions>()
        every { optionsPersistence.updateBudgetOptions(partnerId, capture(slotOptions)) } answers {}
        every { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) } returns setOf(
            adjustableRate(type = OTHER_COSTS_ON_STAFF_COSTS, rate = 10)
        )

        val options = ProjectPartnerBudgetOptions(
            partnerId = partnerId,
            otherCostsOnStaffCostsFlatRate = 5
        )
        updateBudgetOptions.updateBudgetOptions(partnerId, options)

        verify(atLeast = 1) { optionsPersistence.deleteTravelAndAccommodationCosts(partnerId) }
        verify(atLeast = 1) { optionsPersistence.deleteEquipmentCosts(partnerId) }
        verify(atLeast = 1) { optionsPersistence.deleteExternalCosts(partnerId) }
        verify(atLeast = 1) { optionsPersistence.deleteInfrastructureCosts(partnerId) }
        verify(atLeast = 1) { optionsPersistence.deleteUnitCosts(partnerId) }
        verify(atLeast = 1) { optionsPersistence.updateBudgetOptions(partnerId, capture(slotOptions)) }
        verify(atLeast = 1) { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(partnerId) }
        confirmVerified(optionsPersistence, callFlatRateSetupPersistence)


        assertThat(slotOptions.captured).isEqualTo(options)
    }

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
