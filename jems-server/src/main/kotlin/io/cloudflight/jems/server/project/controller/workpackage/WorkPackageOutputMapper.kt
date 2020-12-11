package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.WorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.WorkPackageOutputUpdateDTO
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate

fun WorkPackageOutputUpdateDTO.toWorkPackageOutputUpdate() = WorkPackageOutputUpdate(
    outputNumber = outputNumber,
    programmeOutputIndicatorId = programmeOutputIndicatorId,
    title = title,
    targetValue = targetValue,
    periodNumber = periodNumber,
    description = description
)

fun WorkPackageOutput.toWorkPackageOutputDTO() = WorkPackageOutputDTO(
    outputNumber = outputNumber,
    programmeOutputIndicator = programmeOutputIndicator,
    title = title,
    targetValue = targetValue,
    periodNumber = periodNumber,
    description = description
)

fun Set<WorkPackageOutputUpdateDTO>.toWorkPackageOutputUpdateSet() = this.map { it.toWorkPackageOutputUpdate() }.toSet()

fun Set<WorkPackageOutput>.toWorkPackageOutputDTOSet() = this.map { it.toWorkPackageOutputDTO() }.toSet()

