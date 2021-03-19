package io.cloudflight.jems.server.programme.service.priority.delete_priority

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.validator.checkNoCallExistsForRemovedSpecificObjectives
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePriority(
    private val persistence: ProgrammePriorityPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
) : DeletePriorityInteractor {

    @Transactional(readOnly = true)
    @CanUpdateProgrammeSetup
    override fun deletePriority(priorityId: Long) {
        if (isProgrammeSetupLocked.isLocked())
            throw DeletionWhenProgrammeSetupRestricted()

        checkNoCallExistsForRemovedSpecificObjectives(
            objectivePoliciesToBeRemoved = persistence.getPriorityById(priorityId).getSpecificObjectivePolicies(),
            alreadyUsedObjectivePolicies = persistence.getObjectivePoliciesAlreadyInUse()
        )
        persistence.delete(priorityId)
    }

}
