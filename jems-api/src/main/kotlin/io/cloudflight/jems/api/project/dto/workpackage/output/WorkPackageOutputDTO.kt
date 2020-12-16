package io.cloudflight.jems.api.project.dto.workpackage.output

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorOutputDto

data class WorkPackageOutputDTO(

    val outputNumber: Int,
    val programmeOutputIndicator: IndicatorOutputDto? = null,
    val title: String? = null,
    val targetValue: String? = null,
    val periodNumber: Int? = null,
    val description: String? = null,

    )
