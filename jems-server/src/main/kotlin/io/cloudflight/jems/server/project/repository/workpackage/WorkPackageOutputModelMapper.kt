package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.programme.service.indicator.toIndicatorOutputDto
import io.cloudflight.jems.server.project.entity.ProjectPeriod
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackage
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate


fun WorkPackageOutputUpdate.toEntity(
    indicatorOutput: IndicatorOutput?,
    workPackage: WorkPackage,
    projectPeriod: ProjectPeriod?
) = WorkPackageOutputEntity(
    workPackage = workPackage,
    outputNumber = outputNumber,
    programmeOutputIndicator = indicatorOutput,
    title = title,
    targetValue = targetValue,
    period = projectPeriod,
    description = description
)

fun WorkPackageOutputEntity.toWorkPackageOutput() = WorkPackageOutput(
    outputNumber = outputNumber,
    programmeOutputIndicator = programmeOutputIndicator?.toIndicatorOutputDto(),
    title = title,
    targetValue = targetValue,
    periodNumber = period?.id?.number,
    description = description
)

fun Set<WorkPackageOutputEntity>.toWorkPackageOutputSet() =
    this.map { it.toWorkPackageOutput() }.sortedBy { it.outputNumber }.toSet()