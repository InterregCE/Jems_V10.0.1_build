package io.cloudflight.jems.api.programme.dto.priority

data class ProgrammeSpecificObjectiveDTO(
    val programmeObjectivePolicy: ProgrammeObjectivePolicy,
    val code: String,
    val officialCode: String? = null,
    val dimensionCodes: Map<ProgrammeObjectiveDimensionDTO, List<String>> = emptyMap(),
)
