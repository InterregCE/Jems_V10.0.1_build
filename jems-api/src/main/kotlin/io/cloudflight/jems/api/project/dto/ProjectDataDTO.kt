package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple

data class ProjectDataDTO(
    val title: Set<InputTranslation> = emptySet(),
    val intro: Set<InputTranslation> = emptySet(),
    val duration: Int?,
    val specificObjective: OutputProgrammePriorityPolicySimpleDTO?,
    val programmePriority: OutputProgrammePrioritySimple?
)
