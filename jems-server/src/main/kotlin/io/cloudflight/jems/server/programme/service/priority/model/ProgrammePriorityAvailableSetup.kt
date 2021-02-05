package io.cloudflight.jems.server.programme.service.priority.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy

data class ProgrammePriorityAvailableSetup(
    val freePrioritiesWithPolicies: Map<ProgrammeObjective, List<ProgrammeObjectivePolicy>>,
    val objectivePoliciesAlreadyInUse: Iterable<ProgrammeObjectivePolicy>,
)
