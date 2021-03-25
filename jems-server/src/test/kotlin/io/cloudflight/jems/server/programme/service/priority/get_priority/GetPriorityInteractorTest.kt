package io.cloudflight.jems.server.programme.service.priority.get_priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO2
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.GreenInfrastructure
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriorityAvailableSetup
import io.cloudflight.jems.server.programme.service.priority.testPriority
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetPriorityInteractorTest {

    @MockK
    lateinit var persistence: ProgrammePriorityPersistence

    @InjectMockKs
    private lateinit var getPriority: GetPriority

    @Test
    fun getAllPriorities() {
        every { persistence.getAllMax56Priorities() } returns listOf(testPriority)
        assertThat(getPriority.getAllPriorities()).containsExactly(testPriority.copy())
    }

    @Test
    fun getPriority() {
        every { persistence.getPriorityById(2L) } returns testPriority
        assertThat(getPriority.getPriority(2L)).isEqualTo(testPriority.copy())
    }

    @Test
    fun `getPriority - not existing`() {
        every { persistence.getPriorityById(-1L) } throws ResourceNotFoundException("programmePriority")
        assertThrows<ResourceNotFoundException> { getPriority.getPriority(-1L) }
    }

    @Test
    fun getAvailableSetup() {
        val allValuesWithoutRenewableEnergy = ProgrammeObjectivePolicy.values().toMutableSet()
        allValuesWithoutRenewableEnergy.remove(RenewableEnergy)

        every { persistence.getObjectivePoliciesAlreadySetUp() } returns allValuesWithoutRenewableEnergy
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns listOf(GreenInfrastructure, RenewableEnergy)

        assertThat(getPriority.getAvailableSetup()).isEqualTo(ProgrammePriorityAvailableSetup(
            freePrioritiesWithPolicies = mapOf(PO2 to listOf(RenewableEnergy)),
            objectivePoliciesAlreadyInUse = listOf(GreenInfrastructure, RenewableEnergy),
        ))
    }

}
