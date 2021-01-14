package io.cloudflight.jems.api.project.dto.workpackage.output

import io.cloudflight.jems.api.project.dto.InputTranslation

data class WorkPackageOutputUpdateDTO(

    val outputNumber: Int,
    val programmeOutputIndicatorId: Long? = null,
    val title: Set<InputTranslation> = emptySet(),
    val targetValue: String? = null,
    val periodNumber: Int? = null,
    val description: Set<InputTranslation> = emptySet()

)
