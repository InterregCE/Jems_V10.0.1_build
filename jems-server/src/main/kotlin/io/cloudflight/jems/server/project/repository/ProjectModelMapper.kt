package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.repository.toModel
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.repository.costoption.toModel
import io.cloudflight.jems.server.programme.repository.costoption.toProgrammeUnitCost
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectVersionEntity
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectVersion

fun Set<ProgrammeUnitCostEntity>.toModel() = map { it.toProgrammeUnitCost() }

fun Collection<ProjectPeriodEntity>.toProjectPeriods() = map { it.toProjectPeriod() }

fun ProjectPeriodEntity.toProjectPeriod() = ProjectPeriod(number = id.number, start = start, end = end)

fun ProjectVersionEntity.toProjectVersion() =
    ProjectVersion(version = id.version, projectId = id.projectId, createdAt = createdAt, user = user, status = status)

fun CallEntity.toSettingsModel() = ProjectCallSettings(
    callId = id,
    callName = name,
    startDate = startDate,
    endDate = endDate,
    endDateStep1 = endDateStep1,
    lengthOfPeriod = lengthOfPeriod,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    flatRates = flatRates.toModel(),
    lumpSums = lumpSums.map { it.toModel() }.sortedBy { it.id },
    unitCosts = unitCosts.toProgrammeUnitCost()
)
