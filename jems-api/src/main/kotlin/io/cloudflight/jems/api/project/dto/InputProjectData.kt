package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy

data class InputProjectData(
    val acronym: String?,

    val specificObjective: ProgrammeObjectivePolicy? = null,

    val title: Set<InputTranslation> = emptySet(),

    val duration: Int? = null,

    val intro: Set<InputTranslation> = emptySet()
)
