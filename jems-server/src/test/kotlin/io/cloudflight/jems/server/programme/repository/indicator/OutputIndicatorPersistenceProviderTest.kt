package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.repository.priority.ProgrammeSpecificObjectiveRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional

internal class OutputIndicatorPersistenceProviderTest : IndicatorsPersistenceBaseTest() {


    @MockK
    private lateinit var outputIndicatorRepository: OutputIndicatorRepository

    @MockK
    private lateinit var resultIndicatorRepository: ResultIndicatorRepository

    @MockK
    private lateinit var programmeSpecificObjectiveRepository: ProgrammeSpecificObjectiveRepository

    @InjectMockKs
    private lateinit var outputIndicatorPersistenceProvider: OutputIndicatorPersistenceProvider

    @Test
    fun `should return count of output indicators`() {
        every { outputIndicatorRepository.count() } returns 30
        assertThat(
            outputIndicatorPersistenceProvider.getCountOfOutputIndicators()
        ).isEqualTo(30)
    }

    @Test
    fun `should return output indicator detail`() {
        val outputIndicatorEntity = buildOutputIndicatorEntityInstance()
        every { outputIndicatorRepository.findById(indicatorId) } returns Optional.of(outputIndicatorEntity)
        assertThat(
            outputIndicatorPersistenceProvider.getOutputIndicator(indicatorId)
        ).isEqualTo(outputIndicatorEntity.toOutputIndicatorDetail())
    }

    @Test
    fun `should return set of output indicator summary`() {
        val outputIndicatorEntity = buildOutputIndicatorEntityInstance()
        every { outputIndicatorRepository.findTop50ByOrderById() } returns listOf(outputIndicatorEntity)
        assertThat(
            outputIndicatorPersistenceProvider.getTop50OutputIndicators()
        ).isEqualTo(listOf(outputIndicatorEntity).toOutputIndicatorSummarySet())
    }

    @Test
    fun `should return a page of output indicator detail`() {
        val outputIndicatorEntity = buildOutputIndicatorEntityInstance()
        every { outputIndicatorRepository.findAll(Pageable.unpaged()) } returns PageImpl(listOf(outputIndicatorEntity))
        assertThat(
            outputIndicatorPersistenceProvider.getOutputIndicators(Pageable.unpaged())
        ).isEqualTo(PageImpl(listOf(outputIndicatorEntity)).toOutputIndicatorDetailPage())
    }

    @Test
    fun `should return a list of output indicator summary for a specific objective`() {
        val outputIndicatorEntity = buildOutputIndicatorEntityInstance()
        every {
            outputIndicatorRepository.findAllByProgrammePriorityPolicyEntityProgrammeObjectivePolicyOrderById(
                ProgrammeObjectivePolicy.RenewableEnergy
            )
        } returns listOf(outputIndicatorEntity)
        assertThat(
            outputIndicatorPersistenceProvider.getOutputIndicatorsForSpecificObjective(ProgrammeObjectivePolicy.RenewableEnergy)
        ).isEqualTo(listOf(outputIndicatorEntity).toOutputIndicatorSummaryList())
    }

    @Test
    fun `should save and return the output indicator detail`() {
        val outputIndicator = buildOutputIndicatorInstance()
        val outputIndicatorEntity = buildOutputIndicatorEntityInstance()
        val outputIndicatorEntitySlot= slot<OutputIndicatorEntity>()
        every { programmeSpecificObjectiveRepository.getReferenceIfExistsOrThrow(outputIndicator.programmeObjectivePolicy) } returns indicatorProgrammeSpecificObjectiveEntity
        every { resultIndicatorRepository.getReferenceIfExistsOrThrow(outputIndicator.resultIndicatorId) } returns defaultResultIndicatorEntity
        every {
            outputIndicatorRepository.save(capture(outputIndicatorEntitySlot))
        } returns outputIndicatorEntity
        assertThat(
            outputIndicatorPersistenceProvider.saveOutputIndicator(outputIndicator)
        ).isEqualTo(outputIndicatorEntity.toOutputIndicatorDetail())
        assertThat(outputIndicatorEntitySlot.captured.id).isEqualTo(outputIndicator.id)
    }

    @Test
    fun `should return false when identifier is not used by another output indicator`() {

        val outputIndicatorEntity = buildOutputIndicatorEntityInstance()
        every { outputIndicatorRepository.findOneByIdentifier(indicatorIdentifier) } returns outputIndicatorEntity
        assertThat(
            outputIndicatorPersistenceProvider.isIdentifierUsedByAnotherOutputIndicator(
                outputIndicatorEntity.id,
                outputIndicatorEntity.identifier
            )
        ).isEqualTo(false)
    }

    @Test
    fun `should return true when identifier is used by another output indicator`() {

        val outputIndicatorEntity = buildOutputIndicatorEntityInstance(2L)
        every { outputIndicatorRepository.findOneByIdentifier(indicatorIdentifier) } returns outputIndicatorEntity
        assertThat(
            outputIndicatorPersistenceProvider.isIdentifierUsedByAnotherOutputIndicator(
                indicatorId,
                outputIndicatorEntity.identifier
            )
        ).isEqualTo(true)
    }
}
