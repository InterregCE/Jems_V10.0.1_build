package io.cloudflight.jems.api.programme.dto.priority

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProgrammePriorityDTO(
    val id: Long? = null,
    val code: String,
    val title: Set<InputTranslation> = emptySet(),
    val objective: ProgrammeObjective,
    val specificObjectives: List<ProgrammeSpecificObjectiveDTO> = emptyList()
)
