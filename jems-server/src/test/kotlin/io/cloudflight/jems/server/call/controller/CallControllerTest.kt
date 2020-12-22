package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.STAFF_COSTS
import io.cloudflight.jems.server.call.service.CallService
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.call.service.flatrate.update_flat_rate_setup.UpdateFlatRateSetupInteractor
import io.cloudflight.jems.server.call.service.costoption.update_call_cost_options.UpdateCallCostOptionsInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CallControllerTest {

    @RelaxedMockK
    lateinit var callService: CallService

    @MockK
    lateinit var updateFlatRateSetupInteractor: UpdateFlatRateSetupInteractor

    @MockK
    lateinit var updateCallCostOptionInteractor: UpdateCallCostOptionsInteractor

    @InjectMockKs
    private lateinit var controller: CallController

    @Test
    fun updateCallFlatRateSetup() {
        val slotCallId = slot<Long>()
        val slotFlatRate = slot<Set<ProjectCallFlatRate>>()

        every { updateFlatRateSetupInteractor.updateFlatRateSetup(capture(slotCallId), capture(slotFlatRate)) } answers {}

        val testOptions = FlatRateSetupDTO(
            staffCostFlatRateSetup = FlatRateDTO(rate = 10, isAdjustable = true),
            officeAndAdministrationOnStaffCostsFlatRate = FlatRateDTO(rate = 15, isAdjustable = false),
        )

        controller.updateCallFlatRateSetup(1L, testOptions)

        assertThat(slotCallId.captured).isEqualTo(1L)
        assertThat(slotFlatRate.captured).containsExactlyInAnyOrder(
            ProjectCallFlatRate(type = OFFICE_AND_ADMINISTRATION_ON_STAFF_COSTS, rate = 15, isAdjustable = false),
            ProjectCallFlatRate(type = STAFF_COSTS, rate = 10, isAdjustable = true),
        )
    }

    @Test
    fun updateLumpSums() {
        val slotCallId = slot<Long>()
        val slotLumpSumIds = slot<Set<Long>>()

        every { updateCallCostOptionInteractor.updateLumpSums(capture(slotCallId), capture(slotLumpSumIds)) } answers {}

        controller.updateCallLumpSums(1L, setOf(2, 3))

        assertThat(slotCallId.captured).isEqualTo(1L)
        assertThat(slotLumpSumIds.captured).containsExactlyInAnyOrder(2, 3)
    }

    @Test
    fun updateUnitCosts() {
        val slotCallId = slot<Long>()
        val slotUnitCostIds = slot<Set<Long>>()

        every { updateCallCostOptionInteractor.updateUnitCosts(capture(slotCallId), capture(slotUnitCostIds)) } answers {}

        controller.updateCallUnitCosts(1L, setOf(2, 3))

        assertThat(slotCallId.captured).isEqualTo(1L)
        assertThat(slotUnitCostIds.captured).containsExactlyInAnyOrder(2, 3)
    }

}
