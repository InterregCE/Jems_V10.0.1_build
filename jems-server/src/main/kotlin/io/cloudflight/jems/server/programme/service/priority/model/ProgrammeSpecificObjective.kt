package io.cloudflight.jems.server.programme.service.priority.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy

data class ProgrammeSpecificObjective(
    val programmeObjectivePolicy: ProgrammeObjectivePolicy,
    val code: String,
)
