package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimple
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple

data class OutputProjectData(
    val title: String?,
    val duration: Int?,
    val intro: String?,
    val introProgrammeLanguage: String?,
    val specificObjective: OutputProgrammePriorityPolicySimple?,
    val programmePriority: OutputProgrammePrioritySimple?
)
