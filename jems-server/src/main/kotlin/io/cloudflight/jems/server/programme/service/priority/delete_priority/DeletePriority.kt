package io.cloudflight.jems.server.programme.service.priority.delete_priority

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.programmePriorityDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePriority(
    private val persistence: ProgrammePriorityPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val auditPublisher: ApplicationEventPublisher
) : DeletePriorityInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(DeletePriorityFailed::class)
    override fun deletePriority(priorityId: Long) {
        if (isProgrammeSetupLocked.isLocked())
            throw DeletionWhenProgrammeSetupRestricted()

        val objectivePoliciesToBeRemoved = persistence.getPriorityById(priorityId).getSpecificObjectivePolicies()
        val alreadyUsedObjectivePolicies = persistence.getObjectivePoliciesAlreadyInUse()
        val alreadyUsedByResultIndicatorPolicies = persistence.getObjectivePoliciesAlreadyUsedByResultIndicator()
        val alreadyUsedByOutputIndicatorPolicies = persistence.getObjectivePoliciesAlreadyUsedByOutputIndicator()

        if((objectivePoliciesToBeRemoved intersect alreadyUsedObjectivePolicies).isNotEmpty()
        ) {
            throw ToDeletePriorityAlreadyUsedInCall()
        }

        if((objectivePoliciesToBeRemoved intersect alreadyUsedByResultIndicatorPolicies).isNotEmpty()
        ) {
            throw ToDeletePriorityAlreadyUsedInResultIndicator()
        }

        if((objectivePoliciesToBeRemoved intersect alreadyUsedByOutputIndicatorPolicies).isNotEmpty()
        ) {
            throw ToDeletePriorityAlreadyUsedInOutputIndicator()
        }

        val priorityToBeDeleted = persistence.getPriorityById(priorityId)
        persistence.delete(priorityId).also {
            auditPublisher.publishEvent(
                programmePriorityDeleted(this,
                    programmePriority = priorityToBeDeleted
                )
            )
        }
    }

}
