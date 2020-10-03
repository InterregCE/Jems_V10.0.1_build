package io.cloudflight.jems.server.workpackage.service

import io.cloudflight.jems.api.workpackage.dto.InputWorkPackageCreate
import io.cloudflight.jems.api.workpackage.dto.OutputWorkPackage
import io.cloudflight.jems.api.workpackage.dto.OutputWorkPackageSimple
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.workpackage.entity.WorkPackage

fun WorkPackage.toOutputWorkPackageSimple() = OutputWorkPackageSimple (
    id = id!!,
    number = number,
    name = name
)

fun WorkPackage.toOutputWorkPackage() = OutputWorkPackage (
    id = id!!,
    number = number,
    name = name,
    specificObjective = specificObjective,
    objectiveAndAudience = objectiveAndAudience
)

fun InputWorkPackageCreate.toEntity(project: Project) = WorkPackage (
    id = null,
    name = name,
    project = project,
    specificObjective = specificObjective,
    objectiveAndAudience = objectiveAndAudience
)
