package io.cloudflight.jems.api.project.dto.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation

data class OutputWorkPackage (
    val id: Long,
    val number: Int?,
    val name: Set<InputTranslation> = emptySet(),
    val specificObjective: Set<InputTranslation> = emptySet(),
    val objectiveAndAudience: Set<InputTranslation> = emptySet()
)
