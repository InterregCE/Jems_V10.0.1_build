package io.cloudflight.jems.api.project.dto.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation

data class InputWorkPackageUpdate(

    val id: Long,

    val name: Set<InputTranslation> = emptySet(),

    val specificObjective: Set<InputTranslation> = emptySet(),

    val objectiveAndAudience: Set<InputTranslation> = emptySet()
)
