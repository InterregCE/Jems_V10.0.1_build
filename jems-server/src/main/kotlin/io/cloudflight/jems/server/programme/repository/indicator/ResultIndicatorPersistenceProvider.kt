package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.repository.priority.ProgrammeSpecificObjectiveRepository
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ResultIndicatorPersistenceProvider(
    private val resultIndicatorRepository: ResultIndicatorRepository,
    private val programmeSpecificObjectiveRepository: ProgrammeSpecificObjectiveRepository
) : ResultIndicatorPersistence {

    @Transactional(readOnly = true)
    override fun getCountOfResultIndicators() =
        resultIndicatorRepository.count()

    @Transactional(readOnly = true)
    override fun getResultIndicator(id: Long) =
        resultIndicatorRepository.findById(id).orElseThrow { ResultIndicatorNotFoundException() }
            .toResultIndicatorDetail()

    @Transactional(readOnly = true)
    override fun getTop50ResultIndicators() =
        resultIndicatorRepository.findTop50ByOrderById().toResultIndicatorSummarySet()

    @Transactional(readOnly = true)
    override fun getResultIndicators(pageable: Pageable) =
        resultIndicatorRepository.findAll(pageable).toResultIndicatorDetailPage()

    @Transactional(readOnly = true)
    override fun getResultIndicatorsForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<ResultIndicatorSummary> {
        return resultIndicatorRepository.findAllByProgrammePriorityPolicyEntityProgrammeObjectivePolicyOrderById(
            programmeObjectivePolicy
        ).toResultIndicatorSummaryList()
    }

    @Transactional
    override fun saveResultIndicator(resultIndicator: ResultIndicator) =
        resultIndicatorRepository.save(
            resultIndicator.toResultIndicatorEntity(
                programmeSpecificObjectiveRepository.getReferenceIfExistsOrThrow(
                    resultIndicator.programmeObjectivePolicy
                )
            )
        ).toResultIndicatorDetail()

    @Transactional(readOnly = true)
    override fun isIdentifierUsedByAnotherResultIndicator(resultIndicatorId: Long?, identifier: String) =
        resultIndicatorRepository.findOneByIdentifier(identifier).run {
            !(this == null || this.id == resultIndicatorId)
        }
}
