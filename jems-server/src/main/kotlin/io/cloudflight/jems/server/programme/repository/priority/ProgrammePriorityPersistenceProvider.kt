package io.cloudflight.jems.server.programme.repository.priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammePriorityPersistenceProvider(
    private val priorityRepo: ProgrammePriorityRepository,
    private val specificObjectiveRepo: ProgrammeSpecificObjectiveRepository,
) : ProgrammePriorityPersistence {

    @Transactional(readOnly = true)
    override fun getPriorityById(priorityId: Long): ProgrammePriority =
        getPriorityOrThrow(priorityId).toModel()

    @Transactional(readOnly = true)
    override fun getAllMax45Priorities(): List<ProgrammePriority> =
        priorityRepo.findTop45ByOrderByCodeAsc().map { it.toModel() }

    @Transactional
    override fun create(priority: ProgrammePriority): ProgrammePriority {
        val priorityCreated = priorityRepo.save(priority.toEntity())

        return priorityRepo.save(
            priorityCreated.copy(translatedValues = combineTranslatedValues(priorityCreated.id, priority.title))
        ).toModel()
    }

    @Transactional
    override fun update(priority: ProgrammePriority): ProgrammePriority {
        if (priorityRepo.existsById(priority.id!!)) {
            return priorityRepo.save(
                priority.toEntity().copy(
                    translatedValues = combineTranslatedValues(priority.id, priority.title)
                )).toModel()
        }
        else throw ResourceNotFoundException("programmePriority")
    }

    @Transactional
    override fun delete(priorityId: Long) =
        priorityRepo.delete(getPriorityOrThrow(priorityId))

    @Transactional(readOnly = true)
    override fun getPriorityIdByCode(code: String): Long? =
        priorityRepo.findFirstByCode(code)?.id

    @Transactional(readOnly = true)
    override fun getPriorityIdForPolicyIfExists(policy: ProgrammeObjectivePolicy): Long? =
        specificObjectiveRepo.getPriorityIdForPolicyIfExists(policy)

    @Transactional(readOnly = true)
    override fun getSpecificObjectivesByCodes(specificObjectiveCodes: Collection<String>): List<ProgrammeSpecificObjective> =
        specificObjectiveRepo.findAllByCodeIn(specificObjectiveCodes).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getPrioritiesBySpecificObjectiveCodes(specificObjectiveCodes: Collection<String>): List<ProgrammePriority> =
        specificObjectiveRepo.findAllByCodeIn(specificObjectiveCodes)
            .mapNotNullTo(HashSet()) { it.programmePriority }
            .map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getObjectivePoliciesAlreadySetUp(): Iterable<ProgrammeObjectivePolicy> =
        specificObjectiveRepo.findTop45ByOrderByProgrammeObjectivePolicy().map { it.programmeObjectivePolicy }

    @Transactional(readOnly = true)
    override fun getObjectivePoliciesAlreadyInUse(): Iterable<ProgrammeObjectivePolicy> =
        specificObjectiveRepo.getObjectivePoliciesAlreadyInUse().map { ProgrammeObjectivePolicy.valueOf(it) }

    private fun getPriorityOrThrow(priorityId: Long) =
        priorityRepo.findById(priorityId).orElseThrow { ResourceNotFoundException("programmePriority") }

}
