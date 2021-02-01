package io.cloudflight.jems.server.programme.service.priority.update_priority

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.validator.checkNoCallExistsForRemovedSpecificObjectives
import io.cloudflight.jems.server.programme.service.priority.validator.validateUpdateHasUniqueCodeAndTitle
import io.cloudflight.jems.server.programme.service.priority.validator.validateUpdateProgrammePriority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePriority(
    private val persistence: ProgrammePriorityPersistence,
) : UpdatePriorityInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    override fun updatePriority(priorityId: Long, priority: ProgrammePriority): ProgrammePriority {
        val existingPriority = persistence.getPriorityById(priorityId)

        validateUpdateProgrammePriority(
            programmePriorityId = priorityId,
            programmePriority = priority,
            getPriorityIdByCode = { persistence.getPriorityIdByCode(it) },
            getPriorityIdByTitle = { persistence.getPriorityIdByTitle(it) },
            getPriorityIdForPolicyIfExists = { persistence.getPriorityIdForPolicyIfExists(it) },
            getPrioritiesBySpecificObjectiveCodes = { persistence.getPrioritiesBySpecificObjectiveCodes(it) },
        )

        checkNoCallExistsForRemovedSpecificObjectives(
            newObjectivePolicies = priority.specificObjectives.mapTo(HashSet()) { it.programmeObjectivePolicy },
            existingPriority = existingPriority,
            alreadyUsedObjectivePolicies = persistence.getObjectivePoliciesAlreadyInUse()
        )

        return persistence.update(priority.copy(id = priorityId))
    }

}
