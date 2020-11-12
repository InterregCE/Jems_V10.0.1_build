package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate

fun ProjectCallFlatRate.toEntity() = ProjectCallFlatRateEntity(
    setupId = FlatRateSetupId(callId = callId, type = type),
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<ProjectCallFlatRate>.toEntity() = mapTo(HashSet()) { it.toEntity() }

fun ProjectCallFlatRateEntity.toProjectCallFlatRate() = ProjectCallFlatRate(
    callId = setupId.callId,
    type = setupId.type,
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<ProjectCallFlatRateEntity>.toProjectCallFlatRate() = mapTo(HashSet()) { it.toProjectCallFlatRate() }
