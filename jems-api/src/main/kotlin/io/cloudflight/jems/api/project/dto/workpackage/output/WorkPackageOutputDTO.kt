package io.cloudflight.jems.api.project.dto.workpackage.output

import io.cloudflight.jems.api.project.dto.InputTranslation

data class WorkPackageOutputDTO(
    val outputNumber: Int? = null,
    val title: Set<InputTranslation> = emptySet(),
    val periodNumber: Int? = null,
    val programmeOutputIndicatorId: Long? = null,
    val description: Set<InputTranslation> = emptySet(),
    val targetValue: String? = null
)
