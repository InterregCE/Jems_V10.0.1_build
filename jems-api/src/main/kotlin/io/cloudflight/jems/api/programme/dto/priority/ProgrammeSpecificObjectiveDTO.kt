package io.cloudflight.jems.api.programme.dto.priority

data class ProgrammeSpecificObjectiveDTO(
    val programmeObjectivePolicy: ProgrammeObjectivePolicy,
    val code: String,
    val officialCode: String? = null,
)
