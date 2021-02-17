package io.cloudflight.jems.server.call.repository.costoption

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.repository.flatrate.CallRepository
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.util.Optional

@ExtendWith(MockKExtension::class)
class CallCostOptionsPersistenceTest {

    companion object {
        private val lumpSum2 = ProgrammeLumpSumEntity(
            id = 2,
            name = "testName 2",
            cost = BigDecimal.ONE,
            splittingAllowed = true,
            phase = ProgrammeLumpSumPhase.Closure,
        )
        private val lumpSum3 = ProgrammeLumpSumEntity(
            id = 3,
            name = "testName 3",
            cost = BigDecimal.TEN,
            splittingAllowed = false,
            phase = ProgrammeLumpSumPhase.Preparation,
        )
        private val unitCost2 = ProgrammeUnitCostEntity(
            id = 2,
            name = "testName 2",
            type = "UC 2",
            isOneCostCategory = false,
            costPerUnit = BigDecimal.ZERO,
        )
        private val unitCost3 = ProgrammeUnitCostEntity(
            id = 3,
            name = "testName 3",
            type = "UC 3",
            isOneCostCategory = false,
            costPerUnit = BigDecimal.ONE,
        )
    }

    @MockK
    lateinit var callRepository: CallRepository

    @MockK
    lateinit var programmeLumpSumRepo: ProgrammeLumpSumRepository

    @MockK
    lateinit var programmeUnitCostRepo: ProgrammeUnitCostRepository

    @InjectMockKs
    private lateinit var persistence: CallCostOptionsPersistenceProvider

    @Test
    fun `update lump sum - not-existing call`() {
        every { programmeLumpSumRepo.findAllById(any()) } returns emptyList()
        every { callRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.updateProjectCallLumpSum(-1, emptySet()) }
        assertThat(ex.entity).isEqualTo("call")
    }

    @Test
    fun `update lump sum - not-existing programme lump sum`() {
        every { callRepository.findById(eq(1)) } returns Optional.of(callWithId(1))
        every { programmeLumpSumRepo.findAllById(any()) } returns listOf(lumpSum2)
        val ex = assertThrows<ResourceNotFoundException> {
            persistence.updateProjectCallLumpSum(1, setOf(2, 3))
        }
        assertThat(ex.entity).isEqualTo("programmeLumpSum")
    }

    @Test
    fun `update lump sum - OK`() {
        val slotCall = slot<CallEntity>()
        every { callRepository.findById(eq(1)) } returns Optional.of(callWithId(1))
        every { callRepository.save(capture(slotCall)) } returnsArgument 0
        every { programmeLumpSumRepo.findAllById(any()) } returns listOf(lumpSum2, lumpSum3)

        persistence.updateProjectCallLumpSum(1, setOf(2, 3))

        assertThat(slotCall.captured.lumpSums).containsExactlyInAnyOrder(
            lumpSum2.copy(),
            lumpSum3.copy(),
        )
    }

    @Test
    fun `get lump sums - not existing call`() {
        every { callRepository.findById(-1) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getProjectCallLumpSum(-1) }
        assertThat(ex.entity).isEqualTo("call")
    }

    @Test
    fun `get lump sums - OK`() {
        val call = callWithId(1).copy(lumpSums = setOf(lumpSum2, lumpSum3))
        every { callRepository.findById(1) } returns Optional.of(call)
        assertThat(persistence.getProjectCallLumpSum(1)).containsExactlyInAnyOrder(
            ProgrammeLumpSum(
                id = 2,
                name = "testName 2",
                cost = BigDecimal.ONE,
                splittingAllowed = true,
                phase = ProgrammeLumpSumPhase.Closure,
            ),
            ProgrammeLumpSum(
                id = 3,
                name = "testName 3",
                cost = BigDecimal.TEN,
                splittingAllowed = false,
                phase = ProgrammeLumpSumPhase.Preparation,
            ),
        )
    }

    @Test
    fun `update unit cost - not-existing call`() {
        every { programmeUnitCostRepo.findAllById(any()) } returns emptyList()
        every { callRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.updateProjectCallUnitCost(-1, emptySet()) }
        assertThat(ex.entity).isEqualTo("call")
    }

    @Test
    fun `update unit cost - not-existing programme unit cost`() {
        every { callRepository.findById(eq(1)) } returns Optional.of(callWithId(1))
        every { programmeUnitCostRepo.findAllById(any()) } returns listOf(unitCost2)
        val ex = assertThrows<ResourceNotFoundException> {
            persistence.updateProjectCallUnitCost(1, setOf(2, 3))
        }
        assertThat(ex.entity).isEqualTo("programmeUnitCost")
    }

    @Test
    fun `update unit cost - OK`() {
        val slotCall = slot<CallEntity>()
        every { callRepository.findById(eq(1)) } returns Optional.of(callWithId(1))
        every { callRepository.save(capture(slotCall)) } returnsArgument 0
        every { programmeUnitCostRepo.findAllById(any()) } returns listOf(unitCost2, unitCost3)

        persistence.updateProjectCallUnitCost(1, setOf(2, 3))

        assertThat(slotCall.captured.unitCosts).containsExactlyInAnyOrder(
            unitCost2.copy(),
            unitCost3.copy(),
        )
    }

    @Test
    fun `get unit costs - not existing call`() {
        every { callRepository.findById(-1) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getProjectCallUnitCost(-1) }
        assertThat(ex.entity).isEqualTo("call")
    }

    @Test
    fun `get unit costs - OK`() {
        val call = callWithId(1).copy(unitCosts = setOf(unitCost2, unitCost3))
        every { callRepository.findById(1) } returns Optional.of(call)
        assertThat(persistence.getProjectCallUnitCost(1)).containsExactlyInAnyOrder(
            ProgrammeUnitCost(
                id = 2,
                name = "testName 2",
                type = "UC 2",
                costPerUnit = BigDecimal.ZERO,
                isOneCostCategory = false
            ),
            ProgrammeUnitCost(
                id = 3,
                name = "testName 3",
                type = "UC 3",
                costPerUnit = BigDecimal.ONE,
                isOneCostCategory = false
            ),
        )
    }

}
