package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimple
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple

data class OutputProjectData(
    val title: Set<InputTranslation> = emptySet(),
    val intro: Set<InputTranslation> = emptySet(),
    val duration: Int?,
    val specificObjective: OutputProgrammePriorityPolicySimple?,
    val programmePriority: OutputProgrammePrioritySimple?
)
