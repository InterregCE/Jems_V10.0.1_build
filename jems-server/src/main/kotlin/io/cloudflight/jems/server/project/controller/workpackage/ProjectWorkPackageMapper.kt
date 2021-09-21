package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.ProjectWorkPackageDTO
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage

fun ProjectWorkPackage.toDto() = ProjectWorkPackageDTO(
    id = id,
    workPackageNumber = workPackageNumber,
    name = name,
    activities = activities.toDto(),
    outputs = outputs.toDto(),
)

fun List<ProjectWorkPackage>.toDto() = map { it.toDto() }
