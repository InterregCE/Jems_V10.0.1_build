package io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorOutputDto

data class OutputWorkPackageOutput(

    val outputNumber: Int,
    val programmeOutputIndicator: IndicatorOutputDto? = null,
    val title: String? = null,
    val targetValue: String? = null,
    val periodNumber: Int? = null,
    val description: String? = null,

    )