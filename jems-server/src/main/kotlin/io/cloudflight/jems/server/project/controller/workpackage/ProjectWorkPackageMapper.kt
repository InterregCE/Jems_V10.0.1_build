package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.ProjectWorkPackageDTO
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import org.springframework.data.domain.Page

fun ProjectWorkPackage.toDto() = ProjectWorkPackageDTO(
    id = id,
    workPackageNumber = workPackageNumber,
    name = translatedValues.extractField { it.name },
    activities = activities.toDto(),
    outputs = outputs.toDto(),
)

fun Page<ProjectWorkPackage>.toDto() = map { it.toDto() }
