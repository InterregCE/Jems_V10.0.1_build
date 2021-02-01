package io.cloudflight.jems.server.programme.service.priority.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective

data class ProgrammePriority(
    val id: Long? = null,
    val code: String,
    val title: String,
    val objective: ProgrammeObjective,
    val specificObjectives: List<ProgrammeSpecificObjective> = emptyList(),
)
