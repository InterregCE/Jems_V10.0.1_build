package io.cloudflight.jems.api.programme.dto.indicator

data class IndicatorOutputDto (

    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: String,
    val programmePriorityCode: String?,
    val measurementUnit: String?

)
