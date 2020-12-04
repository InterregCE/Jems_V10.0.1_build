package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OfficeOnStaff
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.StaffCost
import io.cloudflight.jems.server.call.service.CallService
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.call.service.flatrate.update_flat_rate_setup.UpdateFlatRateSetupInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CallControllerTest {

    @RelaxedMockK
    lateinit var callService: CallService

    @MockK
    lateinit var updateCallInteractor: UpdateFlatRateSetupInteractor

    private lateinit var controller: CallController

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        controller = CallController(
            callService,
            updateCallInteractor,
        )
    }

    @Test
    fun updateCallFlatRateSetup() {
        val slotCallId = slot<Long>()
        val slotFlatRate = slot<Set<ProjectCallFlatRate>>()

        every { updateCallInteractor.updateFlatRateSetup(capture(slotCallId), capture(slotFlatRate)) } answers {}

        val testOptions = FlatRateSetupDTO(
            staffCostFlatRateSetup = FlatRateDTO(rate = 10, isAdjustable = true),
            officeOnStaffFlatRateSetup = FlatRateDTO(rate = 15, isAdjustable = false),
        )

        controller.updateCallFlatRateSetup(1L, testOptions)

        assertThat(slotCallId.captured).isEqualTo(1L)
        assertThat(slotFlatRate.captured).containsExactlyInAnyOrder(
            ProjectCallFlatRate(type = OfficeOnStaff, rate = 15, isAdjustable = false),
            ProjectCallFlatRate(type = StaffCost, rate = 10, isAdjustable = true),
        )
    }
}
