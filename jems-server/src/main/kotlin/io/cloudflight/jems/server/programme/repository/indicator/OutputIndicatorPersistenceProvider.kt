package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.repository.priority.ProgrammeSpecificObjectiveRepository
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class OutputIndicatorPersistenceProvider(
    private val outputIndicatorRepository: OutputIndicatorRepository,
    private val resultIndicatorRepository: ResultIndicatorRepository,
    private val programmeSpecificObjectiveRepository: ProgrammeSpecificObjectiveRepository
) : OutputIndicatorPersistence {

    @Transactional(readOnly = true)
    override fun getCountOfOutputIndicators() =
        outputIndicatorRepository.count()

    @Transactional(readOnly = true)
    override fun getOutputIndicator(id: Long): OutputIndicatorDetail =
        outputIndicatorRepository.findById(id).orElseThrow { OutputIndicatorNotFoundException() }
            .toOutputIndicatorDetail()

    @Transactional(readOnly = true)
    override fun getTop250OutputIndicators() =
        outputIndicatorRepository.findTop250ByOrderById().toOutputIndicatorSummarySet()

    @Transactional(readOnly = true)
    override fun getOutputIndicators(pageable: Pageable) =
        outputIndicatorRepository.findAll(pageable).toOutputIndicatorDetailPage()

    @Transactional(readOnly = true)
    override fun getOutputIndicatorsForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy) =
        outputIndicatorRepository.findAllByProgrammePriorityPolicyEntityProgrammeObjectivePolicyOrderById(
            programmeObjectivePolicy
        ).toOutputIndicatorSummaryList()

    @Transactional
    override fun saveOutputIndicator(outputIndicator: OutputIndicator) =
        outputIndicatorRepository.save(
            outputIndicator.toOutputIndicatorEntity(
                outputIndicator.programmeObjectivePolicy?.let { programmeSpecificObjectiveRepository.getReferenceById(it) },
                outputIndicator.getResultIndicatorIfPresent()
            )
        ).toOutputIndicatorDetail()

    @Transactional(readOnly = true)
    override fun isIdentifierUsedByAnotherOutputIndicator(outputIndicatorId: Long?, identifier: String) =
        outputIndicatorRepository.findOneByIdentifier(identifier).run {
            !(this == null || this.id == outputIndicatorId)
        }

    private fun OutputIndicator.getResultIndicatorIfPresent(): ResultIndicatorEntity? {
        if (resultIndicatorId == null || resultIndicatorId == 0L)
            return null
        return resultIndicatorRepository.getReferenceById(resultIndicatorId)
    }

    @Transactional
    override fun deleteOutputIndicator(id: Long) {
        outputIndicatorRepository.deleteById(id)
    }
}
