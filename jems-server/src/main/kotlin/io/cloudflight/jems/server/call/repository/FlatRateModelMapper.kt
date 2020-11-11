package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.server.call.entity.FlatRateSetup
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.service.flatrate.model.FlatRateModel

fun FlatRateModel.toEntity() = FlatRateSetup(
    setupId = FlatRateSetupId(callId = callId, type = type),
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<FlatRateModel>.toEntity() = mapTo(HashSet()) { it.toEntity() }

fun FlatRateSetup.toModel() = FlatRateModel(
    callId = setupId.callId,
    type = setupId.type,
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<FlatRateSetup>.toModel() = mapTo(HashSet()) { it.toModel() }
