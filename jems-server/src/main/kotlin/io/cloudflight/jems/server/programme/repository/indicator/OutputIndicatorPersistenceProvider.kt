package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
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
    override fun getTop50OutputIndicators() =
        outputIndicatorRepository.findTop50ByOrderById().toOutputIndicatorSummarySet()

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
                programmeSpecificObjectiveRepository.getReferenceIfExistsOrThrow(
                    outputIndicator.programmeObjectivePolicy
                ),
                resultIndicatorRepository.getReferenceIfExistsOrThrow(outputIndicator.resultIndicatorId)
            )
        ).toOutputIndicatorDetail()

    @Transactional(readOnly = true)
    override fun isIdentifierUsedByAnotherOutputIndicator(outputIndicatorId: Long?, identifier: String) =
        outputIndicatorRepository.findOneByIdentifier(identifier).run {
            !(this == null || this.id == outputIndicatorId)
        }
}
