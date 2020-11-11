package io.cloudflight.jems.server.call.service.flatrate

import io.cloudflight.jems.api.call.dto.flatrate.InputCallFlatRateSetup
import io.cloudflight.jems.server.call.service.flatrate.model.FlatRateModel

fun InputCallFlatRateSetup.toModel(callId: Long) = FlatRateModel(
    callId = callId,
    type = type,
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<InputCallFlatRateSetup>.toModel(callId: Long) = mapTo(HashSet()) { it.toModel(callId) }

fun FlatRateModel.toOutputDto() = InputCallFlatRateSetup(
    type = type,
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<FlatRateModel>.toOutputDto() = map { it.toOutputDto() }
