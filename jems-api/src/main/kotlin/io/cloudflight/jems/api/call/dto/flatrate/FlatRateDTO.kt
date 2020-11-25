package io.cloudflight.jems.api.call.dto.flatrate

data class FlatRateDTO (
    val rate: Int,
    val isAdjustable: Boolean = true
)
