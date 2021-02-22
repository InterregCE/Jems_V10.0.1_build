package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.repository.priority.ProgrammeSpecificObjectiveRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional

internal class ResultIndicatorPersistenceProviderTest : IndicatorsPersistenceBaseTest() {

    @MockK
    private lateinit var resultIndicatorRepository: ResultIndicatorRepository

    @MockK
    private lateinit var programmeSpecificObjectiveRepository: ProgrammeSpecificObjectiveRepository

    @InjectMockKs
    private lateinit var resultIndicatorPersistenceProvider: ResultIndicatorPersistenceProvider

    @Test
    fun `should return count of result indicators`() {
        every { resultIndicatorRepository.count() } returns 10
        Assertions.assertThat(
            resultIndicatorPersistenceProvider.getCountOfResultIndicators()
        ).isEqualTo(10)
    }

    @Test
    fun `should return result indicator detail`() {
        val resultIndicatorEntity = buildResultIndicatorEntityInstance()
        every { resultIndicatorRepository.findById(indicatorId) } returns Optional.of(resultIndicatorEntity)
        Assertions.assertThat(
            resultIndicatorPersistenceProvider.getResultIndicator(indicatorId)
        ).isEqualTo(resultIndicatorEntity.toResultIndicatorDetail())
    }

    @Test
    fun `should return set of result indicator summary`() {
        val resultIndicatorEntity = buildResultIndicatorEntityInstance()
        every { resultIndicatorRepository.findTop50ByOrderById() } returns listOf(resultIndicatorEntity)
        Assertions.assertThat(
            resultIndicatorPersistenceProvider.getTop50ResultIndicators()
        ).isEqualTo(listOf(resultIndicatorEntity).toResultIndicatorSummarySet())
    }

    @Test
    fun `should return a page of result indicator detail`() {
        val resultIndicatorEntity = buildResultIndicatorEntityInstance()
        every { resultIndicatorRepository.findAll(Pageable.unpaged()) } returns PageImpl(listOf(resultIndicatorEntity))
        Assertions.assertThat(
            resultIndicatorPersistenceProvider.getResultIndicators(Pageable.unpaged())
        ).isEqualTo(PageImpl(listOf(resultIndicatorEntity)).toResultIndicatorDetailPage())
    }

    @Test
    fun `should return a list of result indicator summary for a specific objective`() {
        val resultIndicatorEntity = buildResultIndicatorEntityInstance()
        every {
            resultIndicatorRepository.findAllByProgrammePriorityPolicyEntityProgrammeObjectivePolicyOrderById(
                ProgrammeObjectivePolicy.RenewableEnergy
            )
        } returns PageImpl(listOf(resultIndicatorEntity))
        Assertions.assertThat(
            resultIndicatorPersistenceProvider.getResultIndicatorsForSpecificObjective(ProgrammeObjectivePolicy.RenewableEnergy)
        ).isEqualTo(listOf(resultIndicatorEntity).toResultIndicatorSummaryList())
    }

    @Test
    fun `should save and return the result indicator detail`() {
        val resultIndicator = buildResultIndicatorInstance()
        val resultIndicatorEntity = buildResultIndicatorEntityInstance()
        every { programmeSpecificObjectiveRepository.getReferenceIfExistsOrThrow(resultIndicator.programmeObjectivePolicy) } returns indicatorProgrammeSpecificObjectiveEntity
        every {
            resultIndicatorRepository.save(
                resultIndicator.toResultIndicatorEntity(indicatorProgrammeSpecificObjectiveEntity)
            )
        } returns resultIndicatorEntity
        Assertions.assertThat(
            resultIndicatorPersistenceProvider.saveResultIndicator(resultIndicator)
        ).isEqualTo(resultIndicatorEntity.toResultIndicatorDetail())
    }

    @Test
    fun `should return false when identifier is not used by another result indicator`() {

        val resultIndicatorEntity = buildResultIndicatorEntityInstance()
        every { resultIndicatorRepository.findOneByIdentifier(indicatorIdentifier) } returns resultIndicatorEntity
        Assertions.assertThat(
            resultIndicatorPersistenceProvider.isIdentifierUsedByAnotherResultIndicator(
                resultIndicatorEntity.id,
                resultIndicatorEntity.identifier
            )
        ).isEqualTo(false)
    }

    @Test
    fun `should return true when identifier is used by another result indicator`() {

        val resultIndicatorEntity = buildResultIndicatorEntityInstance(2L)
        every { resultIndicatorRepository.findOneByIdentifier(indicatorIdentifier) } returns resultIndicatorEntity
        Assertions.assertThat(
            resultIndicatorPersistenceProvider.isIdentifierUsedByAnotherResultIndicator(
                indicatorId,
                resultIndicatorEntity.identifier
            )
        ).isEqualTo(true)
    }
}
