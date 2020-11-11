package io.cloudflight.jems.server.call.service.flatrate.model

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType

data class FlatRateModel (
    val callId: Long,
    val type: FlatRateType,
    val rate: Int,
    val isAdjustable: Boolean
)
