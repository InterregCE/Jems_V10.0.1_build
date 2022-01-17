package io.cloudflight.jems.api.programme.dto.priority

data class ProgrammePriorityAvailableSetupDTO(
    val freePrioritiesWithPolicies: Map<ProgrammeObjective, List<ProgrammeSpecificObjectiveDTO>>,
    val objectivePoliciesAlreadyInUse: Iterable<ProgrammeObjectivePolicy>,
)
