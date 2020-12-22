package io.cloudflight.jems.server.call.service.flatrate.update_flat_rate_setup

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateFlatRateSetupInteractorTest {

    @MockK
    lateinit var persistence: CallFlatRateSetupPersistence

    @RelaxedMockK
    lateinit var auditService: AuditService

    private lateinit var updateFlatRateSetupInteractor: UpdateFlatRateSetupInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        updateFlatRateSetupInteractor = UpdateFlatRateSetup(persistence)
        every { persistence.updateProjectCallFlatRate(eq(1), any()) } answers {}
    }

    @Test
    fun `updateFlatRateSetup valid`() {
        val toBeSet = ProjectCallFlatRate(
            type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
            rate = 5,
            isAdjustable = true
        )
        updateFlatRateSetupInteractor.updateFlatRateSetup(1, setOf(toBeSet))

        val event = slot<Set<ProjectCallFlatRate>>()
        verify { persistence.updateProjectCallFlatRate(1, capture(event)) }
        assertThat(event.captured).containsExactly(
            ProjectCallFlatRate(
                type = toBeSet.type,
                rate = toBeSet.rate,
                isAdjustable = toBeSet.isAdjustable
            )
        )
    }

    @Test
    fun `updateFlatRateSetup invalid - duplicates`() {
        val toBeSet = setOf(
            ProjectCallFlatRate(
                type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
                rate = 5,
                isAdjustable = true
            ),
            ProjectCallFlatRate(
                type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
                rate = 9,
                isAdjustable = false
            )
        )
        val ex = assertThrows<I18nValidationException> { updateFlatRateSetupInteractor.updateFlatRateSetup(1, toBeSet) }
        assertThat(ex.i18nKey).isEqualTo("call.flatRateSetup.duplicates")
    }

    @Test
    fun `updateFlatRateSetup invalid - over max flat rate`() {
        val toBeSet = setOf(
            ProjectCallFlatRate(
                type = FlatRateType.STAFF_COSTS,
                rate = 21,
                isAdjustable = true,
            ),
            ProjectCallFlatRate(
                type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS,
                rate = 16,
                isAdjustable = true,
            ),
            ProjectCallFlatRate(
                type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
                rate = 26,
                isAdjustable = true,
            ),
            ProjectCallFlatRate(
                type = FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS,
                rate = 16,
                isAdjustable = true,
            ),
            ProjectCallFlatRate(
                type = FlatRateType.OTHER_COSTS_ON_STAFF_COSTS,
                rate = 41,
                isAdjustable = true,
            )
        )
        val ex = assertThrows<I18nValidationException> { updateFlatRateSetupInteractor.updateFlatRateSetup(1, toBeSet) }
        assertThat(ex.i18nKey).isEqualTo("call.flatRateSetup.rate.out.of.range")
        assertThat(ex.i18nFieldErrors).isEqualTo(mapOf(
            FlatRateType.STAFF_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            FlatRateType.OTHER_COSTS_ON_STAFF_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range")
        ))
    }

    @Test
    fun `updateFlatRateSetup invalid - below min flat rate`() {
        val toBeSet = setOf(
            ProjectCallFlatRate(
                type = FlatRateType.STAFF_COSTS,
                rate = 0,
                isAdjustable = true,
            ),
            ProjectCallFlatRate(
                type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS,
                rate = 0,
                isAdjustable = true,
            ),
            ProjectCallFlatRate(
                type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
                rate = 0,
                isAdjustable = true,
            ),
            ProjectCallFlatRate(
                type = FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS,
                rate = 0,
                isAdjustable = true,
            ),
            ProjectCallFlatRate(
                type = FlatRateType.OTHER_COSTS_ON_STAFF_COSTS,
                rate = 0,
                isAdjustable = true,
            )
        )
        val ex = assertThrows<I18nValidationException> { updateFlatRateSetupInteractor.updateFlatRateSetup(1, toBeSet) }
        assertThat(ex.i18nKey).isEqualTo("call.flatRateSetup.rate.out.of.range")
        assertThat(ex.i18nFieldErrors).isEqualTo(mapOf(
            FlatRateType.OTHER_COSTS_ON_STAFF_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            FlatRateType.TRAVEL_AND_ACCOMMODATION_ON_STAFF_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            FlatRateType.STAFF_COSTS.name to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range")
        ))
    }

}
