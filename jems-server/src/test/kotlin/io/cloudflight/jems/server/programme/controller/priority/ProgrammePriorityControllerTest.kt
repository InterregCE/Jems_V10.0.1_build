package io.cloudflight.jems.server.programme.controller.priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO2
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Growth
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.IndustrialTransition
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityAvailableSetupDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeSpecificObjectiveDTO
import io.cloudflight.jems.server.programme.service.priority.create_priority.CreatePriorityInteractor
import io.cloudflight.jems.server.programme.service.priority.delete_priority.DeletePriorityInteractor
import io.cloudflight.jems.server.programme.service.priority.get_priority.GetPriorityInteractor
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriorityAvailableSetup
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.programme.service.priority.update_priority.UpdatePriorityInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProgrammePriorityControllerTest {

    companion object {

        private val testPriority = ProgrammePriority(
            code = "PO-01",
            title = "PO-01 title",
            objective = PO1,
            specificObjectives = listOf(
                ProgrammeSpecificObjective(programmeObjectivePolicy = Growth, code = "G"),
                ProgrammeSpecificObjective(programmeObjectivePolicy = IndustrialTransition, code = "IT"),
            ),
        )

        private val expectedPriority = ProgrammePriorityDTO(
            code = testPriority.code,
            title = testPriority.title,
            objective = PO1,
            specificObjectives = listOf(
                ProgrammeSpecificObjectiveDTO(programmeObjectivePolicy = Growth, code = "G"),
                ProgrammeSpecificObjectiveDTO(programmeObjectivePolicy = IndustrialTransition, code = "IT"),
            ),
        )

    }

    @MockK
    lateinit var createPriority: CreatePriorityInteractor

    @MockK
    lateinit var deletePriority: DeletePriorityInteractor

    @MockK
    lateinit var getPriority: GetPriorityInteractor

    @MockK
    lateinit var updatePriority: UpdatePriorityInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammePriorityController

    @Test
    fun `should get Programme Priorities`() {
        every { getPriority.getAllPriorities() } returns listOf(testPriority.copy(id = 1))
        assertThat(controller.get()).containsExactly(expectedPriority.copy(id = 1))
    }

    @Test
    fun `should get Programme Priority by id`() {
        every { getPriority.getPriority(2) } returns testPriority.copy(id = 2)
        assertThat(controller.getById(2)).isEqualTo(expectedPriority.copy(id = 2))
    }

    @Test
    fun `should create a new ProgrammePriority and returns it afterwards`() {
        val slotPriority = slot<ProgrammePriority>()
        every { createPriority.createPriority(capture(slotPriority)) } returnsArgument 0

        assertThat(controller.create(expectedPriority)).isEqualTo(expectedPriority)
        assertThat(slotPriority.captured).isEqualTo(testPriority)
    }

    @Test
    fun `should update existing ProgrammePriority and returns it afterwards`() {
        val slotPriority = slot<ProgrammePriority>()
        every { updatePriority.updatePriority(1, capture(slotPriority)) } returnsArgument 1

        assertThat(controller.update(1, expectedPriority)).isEqualTo(expectedPriority)
        assertThat(slotPriority.captured).isEqualTo(testPriority)
    }

    @Test
    fun `should delete ProgrammePriority`() {
        every { deletePriority.deletePriority(5L) } answers {}
        controller.delete(5L)
        verify { deletePriority.deletePriority(5L) }
    }

    @Test
    fun `should retrieve currently available setup`() {
        every { getPriority.getAvailableSetup() } returns ProgrammePriorityAvailableSetup(
            freePrioritiesWithPolicies = mapOf(PO2 to listOf(RenewableEnergy)),
            objectivePoliciesAlreadyInUse = setOf(Growth),
        )
        assertThat(controller.getAvailableSetup()).isEqualTo(ProgrammePriorityAvailableSetupDTO(
            freePrioritiesWithPolicies = mapOf(PO2 to listOf(RenewableEnergy)),
            objectivePoliciesAlreadyInUse = setOf(Growth)
        ))
    }

}
