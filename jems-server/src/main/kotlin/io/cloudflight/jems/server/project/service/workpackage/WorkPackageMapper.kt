package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.InputWorkPackageOutput
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.OutputWorkPackageOutput
import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.programme.service.indicator.toIndicatorOutputDto
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.ProjectPeriod
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackage
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutput

fun WorkPackage.toOutputWorkPackageSimple() = OutputWorkPackageSimple (
    id = id,
    number = number,
    name = name
)

fun WorkPackage.toOutputWorkPackage() = OutputWorkPackage (
    id = id,
    number = number,
    name = name,
    specificObjective = specificObjective,
    objectiveAndAudience = objectiveAndAudience
)

fun InputWorkPackageCreate.toEntity(project: Project) = WorkPackage (
    name = name,
    project = project,
    specificObjective = specificObjective,
    objectiveAndAudience = objectiveAndAudience
)

fun InputWorkPackageOutput.toEntity(indicatorOutput: IndicatorOutput?, workPackage: WorkPackage, projectPeriod: ProjectPeriod?) = WorkPackageOutput (
    workPackage = workPackage,
    outputNumber = outputNumber,
    programmeOutputIndicator = indicatorOutput,
    title = title,
    targetValue = targetValue,
    period = projectPeriod,
    description = description
)

fun WorkPackageOutput.toOutputWorkPackageOutput() = OutputWorkPackageOutput(
    outputNumber = outputNumber,
    programmeOutputIndicator = programmeOutputIndicator?.toIndicatorOutputDto(),
    title = title,
    targetValue = targetValue,
    periodNumber = period?.id?.number,
    description = description
)
