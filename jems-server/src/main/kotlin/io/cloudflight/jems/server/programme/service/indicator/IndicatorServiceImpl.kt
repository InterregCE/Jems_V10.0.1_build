package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorOutputDto
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorOutputCreate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorOutputUpdate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorResultCreate
import io.cloudflight.jems.api.programme.dto.indicator.InputIndicatorResultUpdate
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorOutput
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorResult
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorOutputRepository
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorResultRepository
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.repository.ProgrammePriorityPolicyRepository
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.controller.indicator.toEntity
import io.cloudflight.jems.server.programme.controller.indicator.toIndicatorOutputDto
import io.cloudflight.jems.server.programme.controller.indicator.toOutputIndicator
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IndicatorServiceImpl(
    private val indicatorResultRepository: IndicatorResultRepository,
    private val indicatorOutputRepository: IndicatorOutputRepository,
    private val programmePriorityPolicyRepository: ProgrammePriorityPolicyRepository,
    private val auditService: AuditService
) : IndicatorService {

    //region INDICATOR OUTPUT

    @Transactional(readOnly = true)
    override fun getOutputIndicatorById(id: Long): OutputIndicatorOutput {
        return indicatorOutputRepository.findById(id).map { it.toOutputIndicator() }
            .orElseThrow { ResourceNotFoundException("indicator_output") }
    }

    @Transactional(readOnly = true)
    override fun getOutputIndicators(pageable: Pageable): Page<OutputIndicatorOutput> {
        return indicatorOutputRepository.findAll(pageable).map { it.toOutputIndicator() }
    }

    @Transactional(readOnly = true)
    override fun getOutputIndicatorsDetails(): Set<IndicatorOutputDto> {
        return indicatorOutputRepository.findAll().map { it.toIndicatorOutputDto() }.toSet()
    }

    @Transactional(readOnly = true)
    override fun existsOutputByIdentifier(identifier: String): Boolean {
        return indicatorOutputRepository.existsByIdentifier(identifier)
    }

    @Transactional
    override fun save(indicator: InputIndicatorOutputCreate): OutputIndicatorOutput {
        val indicatorSaved =  indicatorOutputRepository.save(
            indicator.toEntity(getProgrammePriorityEntity(indicator.programmeObjectivePolicy))
        ).toOutputIndicator()
        auditService.logEvent(indicatorAdded(indicatorSaved.identifier))
        return indicatorSaved
    }

    @Transactional
    override fun save(indicator: InputIndicatorOutputUpdate): OutputIndicatorOutput {
        val indicatorOld = getOutputIndicatorById(indicator.id)
        val indicatorSaved = indicatorOutputRepository.save(
            indicator.toEntity(
                uniqueIdentifier = getIndicatorIdentifierIfUnique(indicatorOld, indicator.identifier!!),
                programmePriorityPolicy = getProgrammePriorityEntity(indicator.programmeObjectivePolicy)
            )
        ).toOutputIndicator()
        auditService.logEvent(indicatorEdited(
            identifier = indicatorSaved.identifier,
            changes = indicatorOld.getDiff(indicatorSaved)
        ))
        return indicatorSaved
    }

    private fun getIndicatorIdentifierIfUnique(oldIndicator: OutputIndicatorOutput, newIdentifier: String): String {
        if (oldIndicator.name == newIdentifier)
            return oldIndicator.name

        val existing = indicatorOutputRepository.findOneByIdentifier(newIdentifier)
        if (existing == null || existing.id == oldIndicator.id)
            return newIdentifier

        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = mapOf("identifier" to I18nFieldError("indicator.identifier.already.in.use"))
        )
    }
    //endregion

    //region INDICATOR RESULT

    @Transactional(readOnly = true)
    override fun getResultIndicatorById(id: Long): OutputIndicatorResult {
        return indicatorResultRepository.findById(id).map { it.toOutputIndicator() }
            .orElseThrow { ResourceNotFoundException("indicator_result") }
    }

    @Transactional(readOnly = true)
    override fun getResultIndicators(pageable: Pageable): Page<OutputIndicatorResult> {
        return indicatorResultRepository.findAll(pageable).map { it.toOutputIndicator() }
    }

    @Transactional(readOnly = true)
    override fun existsResultByIdentifier(identifier: String): Boolean {
        return indicatorResultRepository.existsByIdentifier(identifier)
    }

    @Transactional
    override fun save(indicator: InputIndicatorResultCreate): OutputIndicatorResult {
        val indicatorSaved = indicatorResultRepository.save(
            indicator.toEntity(getProgrammePriorityEntity(indicator.programmeObjectivePolicy))
        ).toOutputIndicator()
        auditService.logEvent(indicatorAdded(indicatorSaved.identifier))
        return indicatorSaved
    }

    @Transactional
    override fun save(indicator: InputIndicatorResultUpdate): OutputIndicatorResult {
        val indicatorOld = getResultIndicatorById(indicator.id)
        val indicatorSaved =  indicatorResultRepository.save(
            indicator.toEntity(
                uniqueIdentifier = getIndicatorIdentifierIfUnique(indicatorOld, indicator.identifier!!),
                programmePriorityPolicy = getProgrammePriorityEntity(indicator.programmeObjectivePolicy)
            )
        ).toOutputIndicator()
        auditService.logEvent(indicatorEdited(
            identifier = indicatorSaved.identifier,
            changes = indicatorOld.getDiff(indicatorSaved)
        ))
        return indicatorSaved
    }

    private fun getIndicatorIdentifierIfUnique(oldIndicator: OutputIndicatorResult, newIdentifier: String): String {
        if (oldIndicator.name == newIdentifier)
            return oldIndicator.name

        val existing = indicatorResultRepository.findOneByIdentifier(newIdentifier)
        if (existing == null || existing.id == oldIndicator.id)
            return newIdentifier

        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = mapOf("identifier" to I18nFieldError("indicator.identifier.already.in.use"))
        )
    }
    //endregion

    private fun getProgrammePriorityEntity(programmeObjectivePolicy: ProgrammeObjectivePolicy?): ProgrammePriorityPolicy? {
        var programmePriorityPolicy: ProgrammePriorityPolicy? = null
        if (programmeObjectivePolicy != null)
            programmePriorityPolicy = programmePriorityPolicyRepository.findById(programmeObjectivePolicy)
                .orElseThrow { ResourceNotFoundException("programme_priority_policy") }
        return programmePriorityPolicy
    }

}
