package io.cloudflight.jems.server.call.repository.flatrate

import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate

fun ProjectCallFlatRate.toEntity(callId: Long) = ProjectCallFlatRateEntity(
    setupId = FlatRateSetupId(callId = callId, type = type),
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<ProjectCallFlatRate>.toEntity(callId: Long) = mapTo(HashSet()) { it.toEntity(callId) }

fun ProjectCallFlatRateEntity.toProjectCallFlatRate() = ProjectCallFlatRate(
    type = setupId.type,
    rate = rate,
    isAdjustable = isAdjustable
)

fun Set<ProjectCallFlatRateEntity>.toProjectCallFlatRate() = mapTo(HashSet()) { it.toProjectCallFlatRate() }
