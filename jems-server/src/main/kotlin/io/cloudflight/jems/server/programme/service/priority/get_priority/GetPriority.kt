package io.cloudflight.jems.server.programme.service.priority.get_priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriorityAvailableSetup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPriority(
    private val persistence: ProgrammePriorityPersistence,
) : GetPriorityInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    override fun getAllPriorities(): List<ProgrammePriority> =
        persistence.getAllMax56Priorities()

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    override fun getPriority(priorityId: Long): ProgrammePriority =
        persistence.getPriorityById(priorityId)

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    override fun getAvailableSetup(): ProgrammePriorityAvailableSetup = ProgrammePriorityAvailableSetup(
        freePrioritiesWithPolicies = getFreePrioritiesWithPolicies(),
        objectivePoliciesAlreadyInUse = persistence.getObjectivePoliciesAlreadyInUse(),
    )

    /**
     * Will give you all objective policies, that are not yet taken by any priority ( = are now free to take)
     */
    private fun getFreePrioritiesWithPolicies(): Map<ProgrammeObjective, List<ProgrammeObjectivePolicy>> {
        val allPolicies = ProgrammeObjectivePolicy.values().toMutableSet()
        // we can just consider all possible and remove all already-taken
        allPolicies.removeAll(persistence.getObjectivePoliciesAlreadySetUp())
        return allPolicies
            .groupBy { it.objective }
            .mapValues { entry -> entry.value.sorted() }
    }

}
