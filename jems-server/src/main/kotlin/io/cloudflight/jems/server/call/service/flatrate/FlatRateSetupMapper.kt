package io.cloudflight.jems.server.call.service.flatrate

import io.cloudflight.jems.api.call.dto.flatrate.InputCallFlatRateSetup
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate

fun InputCallFlatRateSetup.toModel(callId: Long) = ProjectCallFlatRate(
    callId = callId,
    type = type,
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<InputCallFlatRateSetup>.toModel(callId: Long) = mapTo(HashSet()) { it.toModel(callId) }

fun ProjectCallFlatRate.toOutputDto() = InputCallFlatRateSetup(
    type = type,
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<ProjectCallFlatRate>.toOutputDto() = map { it.toOutputDto() }
    .sortedBy { it.type }
