package io.cloudflight.jems.server.call.service.flatrate.update_flat_rate_setup

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.call.dto.flatrate.InputCallFlatRateSetup
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.call.service.flatrate.model.FlatRateModel
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
        every { persistence.updateFlatRateSetup(eq(1), any()) } answers {}
    }

    @Test
    fun `updateFlatRateSetup valid`() {
        val toBeSet = InputCallFlatRateSetup(
            type = FlatRateType.OfficeOnOther,
            rate = 5,
            isAdjustable = true
        )
        updateFlatRateSetupInteractor.updateFlatRateSetup(1, setOf(toBeSet))

        val event = slot<Set<FlatRateModel>>()
        verify { persistence.updateFlatRateSetup(1, capture(event)) }
        assertThat(event.captured).containsExactly(
            FlatRateModel(
                callId = 1,
                type = toBeSet.type,
                rate = toBeSet.rate,
                isAdjustable = toBeSet.isAdjustable
            )
        )
    }

    @Test
    fun `updateFlatRateSetup invalid - duplicates`() {
        val toBeSet = setOf(
            InputCallFlatRateSetup(
                type = FlatRateType.OfficeOnOther,
                rate = 5,
                isAdjustable = true
            ),
            InputCallFlatRateSetup(
                type = FlatRateType.OfficeOnOther,
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
            InputCallFlatRateSetup(
                type = FlatRateType.StaffCost,
                rate = 21
            ),
            InputCallFlatRateSetup(
                type = FlatRateType.OfficeOnStaff,
                rate = 16
            ),
            InputCallFlatRateSetup(
                type = FlatRateType.OfficeOnOther,
                rate = 26
            ),
            InputCallFlatRateSetup(
                type = FlatRateType.TravelOnStaff,
                rate = 16
            ),
            InputCallFlatRateSetup(
                type = FlatRateType.OtherOnStaff,
                rate = 41
            )
        )
        val ex = assertThrows<I18nValidationException> { updateFlatRateSetupInteractor.updateFlatRateSetup(1, toBeSet) }
        assertThat(ex.i18nKey).isEqualTo("call.flatRateSetup.rate.out.of.range")
        assertThat(ex.i18nFieldErrors).isEqualTo(mapOf(
            "StaffCost" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            "OfficeOnStaff" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            "OfficeOnOther" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            "TravelOnStaff" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            "OtherOnStaff" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range")
        ))
    }

    @Test
    fun `updateFlatRateSetup invalid - below min flat rate`() {
        val toBeSet = setOf(
            InputCallFlatRateSetup(
                type = FlatRateType.StaffCost,
                rate = 0
            ),
            InputCallFlatRateSetup(
                type = FlatRateType.OfficeOnStaff,
                rate = 0
            ),
            InputCallFlatRateSetup(
                type = FlatRateType.OfficeOnOther,
                rate = 0
            ),
            InputCallFlatRateSetup(
                type = FlatRateType.TravelOnStaff,
                rate = 0
            ),
            InputCallFlatRateSetup(
                type = FlatRateType.OtherOnStaff,
                rate = 0
            )
        )
        val ex = assertThrows<I18nValidationException> { updateFlatRateSetupInteractor.updateFlatRateSetup(1, toBeSet) }
        assertThat(ex.i18nKey).isEqualTo("call.flatRateSetup.rate.out.of.range")
        assertThat(ex.i18nFieldErrors).isEqualTo(mapOf(
            "OtherOnStaff" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            "TravelOnStaff" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            "OfficeOnOther" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            "OfficeOnStaff" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range"),
            "StaffCost" to I18nFieldError(i18nKey = "call.flatRateSetup.rate.out.of.range")
        ))
    }

}
