package io.cloudflight.jems.server.programme.service.priority.delete_priority

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.validator.checkNoCallExistsForRemovedSpecificObjectives
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePriority(
    private val persistence: ProgrammePriorityPersistence,
) : DeletePriorityInteractor {

    @Transactional(readOnly = true)
    @CanUpdateProgrammeSetup
    override fun deletePriority(priorityId: Long) {
        checkNoCallExistsForRemovedSpecificObjectives(
            newObjectivePolicies = emptySet(),
            existingPriority = persistence.getPriorityById(priorityId),
            alreadyUsedObjectivePolicies = persistence.getObjectivePoliciesAlreadyInUse()
        )
        persistence.delete(priorityId)
    }

}
