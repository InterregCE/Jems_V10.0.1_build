package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.repository.flatrate.toModel
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.programme.repository.costoption.toModel
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import kotlin.collections.HashSet

fun CallEntity.toSettingsModel() = ProjectCallSettings(
    callId = id,
    callName = name,
    startDate = startDate,
    endDate = endDate,
    lengthOfPeriod = lengthOfPeriod,
    flatRates = flatRates.mapTo(HashSet()) { it.toModel() },
    lumpSums = lumpSums.map { it.toModel() }.sortedBy { it.id },
    unitCosts = unitCosts.toModel()
)

fun ProjectEntity.toModel() = Project(
    id = id,
    periods = periods.map { it.toModel() },
)

fun ProjectPeriodEntity.toModel() = ProjectPeriod(
    number = id.number,
    start = start,
    end = end,
)
