package io.cloudflight.jems.server.project.service.workpackage.model

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorOutputDto

data class WorkPackageOutput(

    val outputNumber: Int,
    val programmeOutputIndicator: IndicatorOutputDto? = null,
    val title: String? = null,
    val targetValue: String? = null,
    val periodNumber: Int? = null,
    val description: String? = null,

    )