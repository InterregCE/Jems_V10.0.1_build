package io.cloudflight.ems.indicator.service

import io.cloudflight.ems.api.indicator.dto.InputIndicatorOutputCreate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorOutputUpdate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorResultCreate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorResultUpdate
import io.cloudflight.ems.api.indicator.dto.OutputIndicatorOutput
import io.cloudflight.ems.api.indicator.dto.OutputIndicatorResult
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.indicator.repository.IndicatorOutputRepository
import io.cloudflight.ems.indicator.repository.IndicatorResultRepository
import io.cloudflight.ems.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.ems.programme.repository.ProgrammePriorityPolicyRepository
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.AuditService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IndicatorServiceImpl(
    private val indicatorResultRepository: IndicatorResultRepository,
    private val indicatorOutputRepository: IndicatorOutputRepository,
    private val programmePriorityPolicyRepository: ProgrammePriorityPolicyRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
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
    override fun existsOutputByIdentifier(identifier: String): Boolean {
        return indicatorOutputRepository.existsByIdentifier(identifier)
    }

    @Transactional
    override fun save(indicator: InputIndicatorOutputCreate): OutputIndicatorOutput {
        val indicatorSaved =  indicatorOutputRepository.save(
            indicator.toEntity(getProgrammePriorityEntity(indicator.programmeObjectivePolicy))
        ).toOutputIndicator()
        auditService.logEvent(Audit.indicatorAdded(
            currentUser = securityService.currentUser,
            identifier = indicatorSaved.identifier
        ))
        return indicatorSaved
    }

    @Transactional
    override fun save(indicator: InputIndicatorOutputUpdate): OutputIndicatorOutput {
        val indicatorOld = getOutputIndicatorById(indicator.id)
        val indicatorSaved = indicatorOutputRepository.save(
            indicator.toEntity(getProgrammePriorityEntity(indicator.programmeObjectivePolicy))
        ).toOutputIndicator()
        auditService.logEvent(Audit.indicatorEdited(
            currentUser = securityService.currentUser,
            identifier = indicatorSaved.identifier,
            changes = indicatorOld.getDiff(indicatorSaved)
        ))
        return indicatorSaved
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
        auditService.logEvent(Audit.indicatorAdded(
            currentUser = securityService.currentUser,
            identifier = indicatorSaved.identifier
        ))
        return indicatorSaved
    }

    @Transactional
    override fun save(indicator: InputIndicatorResultUpdate): OutputIndicatorResult {
        val indicatorOld = getResultIndicatorById(indicator.id)
        val indicatorSaved =  indicatorResultRepository.save(
            indicator.toEntity(getProgrammePriorityEntity(indicator.programmeObjectivePolicy))
        ).toOutputIndicator()
        auditService.logEvent(Audit.indicatorEdited(
            currentUser = securityService.currentUser,
            identifier = indicatorSaved.identifier,
            changes = indicatorOld.getDiff(indicatorSaved)
        ))
        return indicatorSaved
    }

    private fun getProgrammePriorityEntity(programmeObjectivePolicy: ProgrammeObjectivePolicy?): ProgrammePriorityPolicy? {
        var programmePriorityPolicy: ProgrammePriorityPolicy? = null
        if (programmeObjectivePolicy != null)
            programmePriorityPolicy = programmePriorityPolicyRepository.findById(programmeObjectivePolicy)
                .orElseThrow { ResourceNotFoundException("programme_priority_policy") }
        return programmePriorityPolicy
    }
    //endregion

}
