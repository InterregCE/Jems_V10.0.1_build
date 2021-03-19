package io.cloudflight.jems.server.programme.service.priority.update_priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.validator.checkNoCallExistsForRemovedSpecificObjectives
import io.cloudflight.jems.server.programme.service.priority.validator.validateUpdateProgrammePriority
import io.cloudflight.jems.server.programme.service.programmePriorityUpdated
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePriority(
    private val persistence: ProgrammePriorityPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val auditService: AuditService,
) : UpdatePriorityInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    override fun updatePriority(priorityId: Long, priority: ProgrammePriority): ProgrammePriority {
        val existingPriority = persistence.getPriorityById(priorityId)

        validateUpdateProgrammePriority(
            programmePriorityId = priorityId,
            programmePriority = priority,
            getPriorityIdByCode = { persistence.getPriorityIdByCode(it) },
            getPriorityIdForPolicyIfExists = { persistence.getPriorityIdForPolicyIfExists(it) },
            getPrioritiesBySpecificObjectiveCodes = { persistence.getPrioritiesBySpecificObjectiveCodes(it) },
        )

        val objectivePoliciesToBeRemoved = extractRemovedSpecificObjectives(priority, existingPriority)
        if (isProgrammeSetupLocked.isLocked() && objectivePoliciesToBeRemoved.isNotEmpty())
            throw UpdateWhenProgrammeSetupRestricted()

        checkNoCallExistsForRemovedSpecificObjectives(
            objectivePoliciesToBeRemoved = objectivePoliciesToBeRemoved,
            alreadyUsedObjectivePolicies = persistence.getObjectivePoliciesAlreadyInUse()
        )

        val newPriority = persistence.update(priority.copy(id = priorityId))
        programmePriorityUpdated(existingPriority, existingPriority.getDiff(newPriority)).logWith(auditService)
        return newPriority
    }

    private fun extractRemovedSpecificObjectives(
        newPriority: ProgrammePriority,
        existingPriority: ProgrammePriority,
    ): Set<ProgrammeObjectivePolicy> {
        val newObjectivePolicies = newPriority.getSpecificObjectivePolicies()

        val objectivePoliciesToBeRemoved = existingPriority.getSpecificObjectivePolicies()
        objectivePoliciesToBeRemoved.removeAll(newObjectivePolicies)

        return objectivePoliciesToBeRemoved
    }

}
