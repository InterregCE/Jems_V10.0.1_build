package io.cloudflight.jems.api.call.dto.flatrate

data class InputCallFlatRateSetup (
    val type: FlatRateType,
    val rate: Int,
    val isAdjustable: Boolean = true
)
