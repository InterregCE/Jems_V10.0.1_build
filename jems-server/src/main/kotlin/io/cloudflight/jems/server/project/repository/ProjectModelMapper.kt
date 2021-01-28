package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.repository.flatrate.toModel
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.repository.costoption.toProgrammeUnitCost
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectPeriod

fun Set<ProgrammeUnitCostEntity>.toProgrammeUnitCosts() = map { it.toProgrammeUnitCost() }

fun Collection<ProjectPeriodEntity>.toProjectPeriods() = map { it.toProjectPeriod() }

fun ProjectPeriodEntity.toProjectPeriod() = ProjectPeriod(number = id.number, start = start, end = end)

fun CallEntity.toSettingsModel() = ProjectCallSettings(
    callId = id,
    callName = name,
    startDate = startDate,
    endDate = endDate,
    lengthOfPeriod = lengthOfPeriod,
    flatRates = flatRates.mapTo(HashSet()) { it.toModel() },
    lumpSums = lumpSums.map { it.toProgrammeUnitCost() }.sortedBy { it.id },
    unitCosts = unitCosts.toProgrammeUnitCost()
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
