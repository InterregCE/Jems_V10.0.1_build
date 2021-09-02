package io.cloudflight.jems.server.project.controller.result

import io.cloudflight.jems.api.project.dto.result.ProjectResultUpdateRequestDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.project.service.result.model.ProjectResult

fun List<ProjectResultUpdateRequestDTO>.toModel() = map {
    ProjectResult(
        programmeResultIndicatorId = it.programmeResultIndicatorId,
        baseline = it.baseline,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        description = it.description
    )
}

fun List<ProjectResult>.toDto() = map {
    ProjectResultDTO(
        resultNumber = it.resultNumber,
        programmeResultIndicatorId = it.programmeResultIndicatorId,
        programmeResultIndicatorIdentifier = it.programmeResultIndicatorIdentifier,
        baseline = it.baseline,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        description = it.description
    )
}
