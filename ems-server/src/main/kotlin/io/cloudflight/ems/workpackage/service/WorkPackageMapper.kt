package io.cloudflight.ems.workpackage.service

import io.cloudflight.ems.api.workpackage.dto.InputWorkPackageCreate
import io.cloudflight.ems.api.workpackage.dto.OutputWorkPackage
import io.cloudflight.ems.api.workpackage.dto.OutputWorkPackageSimple
import io.cloudflight.ems.project.entity.Project
import io.cloudflight.ems.workpackage.entity.WorkPackage

fun WorkPackage.toOutputWorkPackageSimple() = OutputWorkPackageSimple (
    id = id,
    number = number!!,
    name = name!!
)

fun WorkPackage.toOutputWorkPackage() = OutputWorkPackage (
    id = id,
    number = number!!,
    name = name!!,
    specificObjective = specificObjective,
    objectiveAndAudience = objectiveAndAudience
)

fun InputWorkPackageCreate.toEntity(project: Project, number: Int) = WorkPackage (
    id = null,
    name = name,
    project = project,
    number = number,
    specificObjective = specificObjective,
    objectiveAndAudience = objectiveAndAudience
)
