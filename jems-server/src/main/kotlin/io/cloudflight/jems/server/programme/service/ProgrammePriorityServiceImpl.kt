package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityCreate
import io.cloudflight.jems.api.programme.dto.priority.InputProgrammePriorityUpdate
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriority
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimple
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.ProgrammePriorityPolicyRepository
import io.cloudflight.jems.server.programme.repository.ProgrammePriorityRepository
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.authorization.CanReadProgrammeSetup
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgrammePriorityServiceImpl(
    private val programmePriorityRepository: ProgrammePriorityRepository,
    private val programmePriorityPolicyRepository: ProgrammePriorityPolicyRepository,
    private val auditService: AuditService
) : ProgrammePriorityService {

    @CanReadProgrammeSetup
    @Transactional(readOnly = true)
    override fun getAll(page: Pageable): Page<OutputProgrammePriority> {
        return programmePriorityRepository.findAll(page).map { it.toOutputProgrammePriority() }
    }

    @CanUpdateProgrammeSetup
    @Transactional
    override fun create(priority: InputProgrammePriorityCreate): OutputProgrammePriority {
        val savedProgrammePriority = programmePriorityRepository.save(priority.toEntity()).toOutputProgrammePriority()
        auditService.logEvent(programmePriorityAdded(savedProgrammePriority))
        return savedProgrammePriority
    }

    @CanUpdateProgrammeSetup
    @Transactional
    override fun update(priority: InputProgrammePriorityUpdate): OutputProgrammePriority {
        return programmePriorityRepository.save(priority.toEntity()).toOutputProgrammePriority()
    }

    @CanUpdateProgrammeSetup
    @Transactional
    override fun delete(programmePriorityId: Long) {
        programmePriorityRepository.delete(
            programmePriorityRepository.findById(programmePriorityId)
                .orElseThrow { ResourceNotFoundException("programme_priority") }
        )
    }

    @CanReadProgrammeSetup
    @Transactional(readOnly = true)
    override fun getFreePrioritiesWithPolicies(): Map<ProgrammeObjective, List<ProgrammeObjectivePolicy>> {
        val allPolicies = ProgrammeObjectivePolicy.values().toMutableSet()
        // we can just consider all possible and remove all already-taken
        allPolicies.removeAll(
            programmePriorityPolicyRepository.findAll().map { it.programmeObjectivePolicy }
        )
        return allPolicies
            .groupBy { it.objective }
            .mapValues { entry -> entry.value.sorted() }
    }

    @Transactional(readOnly = true)
    override fun getByCode(priorityCode: String): OutputProgrammePriority? {
        return programmePriorityRepository.findFirstByCode(code = priorityCode)?.toOutputProgrammePriority()
    }

    @Transactional(readOnly = true)
    override fun getByTitle(title: String): OutputProgrammePriority? {
        return programmePriorityRepository.findFirstByTitle(title = title)?.toOutputProgrammePriority()
    }

    @Transactional(readOnly = true)
    override fun getPriorityPolicyByCode(code: String): OutputProgrammePriorityPolicySimple? {
        return programmePriorityPolicyRepository.findFirstByCode(code)?.toOutputProgrammePriorityPolicy()
    }

    @Transactional(readOnly = true)
    override fun getPriorityIdForPolicyIfExists(policy: ProgrammeObjectivePolicy): Long? {
        return programmePriorityPolicyRepository.getPriorityIdForPolicyIfExists(policy = policy)
    }

}
