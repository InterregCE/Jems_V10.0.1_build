package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectForm(
    val id: Long,
    val callSettings: ProjectCallSettings,
    val acronym: String,
    val title: Set<InputTranslation>? = emptySet(),
    val intro: Set<InputTranslation>? = emptySet(),
    val duration: Int?,
    val specificObjective: OutputProgrammePriorityPolicySimpleDTO? = null,
    val programmePriority: OutputProgrammePrioritySimple? = null,
    val periods: List<ProjectPeriod> = emptyList()
)
