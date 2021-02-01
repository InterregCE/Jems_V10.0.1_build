package io.cloudflight.jems.api.programme.dto.priority

data class ProgrammePriorityDTO(
    val id: Long? = null,
    val code: String,
    val title: String,
    val objective: ProgrammeObjective,
    val specificObjectives: List<ProgrammeSpecificObjectiveDTO> = emptyList()
)
