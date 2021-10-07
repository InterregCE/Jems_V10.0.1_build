package io.cloudflight.jems.server.project.controller.result

import io.cloudflight.jems.api.project.dto.result.IndicatorOverviewLineDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultUpdateRequestDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.project.service.result.model.IndicatorOverviewLine
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

fun List<IndicatorOverviewLine>.toIndicatorOverviewLinesDto() = map {
    IndicatorOverviewLineDTO(
        outputIndicatorId = it.outputIndicator?.id,
        outputIndicatorIdentifier = it.outputIndicator?.identifier,
        outputIndicatorName = it.outputIndicator?.name,
        outputIndicatorMeasurementUnit = it.outputIndicator?.measurementUnit,
        outputIndicatorTargetValueSumUp = it.outputIndicator?.targetValueSumUp,

        projectOutputNumber = it.projectOutput?.projectOutputNumber,
        projectOutputTitle = it.projectOutput?.projectOutputTitle,
        projectOutputTargetValue = it.projectOutput?.projectOutputTargetValue,

        resultIndicatorId = it.resultIndicator?.id,
        resultIndicatorIdentifier = it.resultIndicator?.identifier,
        resultIndicatorName = it.resultIndicator?.name,
        resultIndicatorMeasurementUnit = it.resultIndicator?.measurementUnit,
        resultIndicatorBaseline = it.resultIndicator?.baseline,
        resultIndicatorTargetValueSumUp = it.resultIndicator?.targetValueSumUp,

        onlyResultWithoutOutputs = it.onlyResultWithoutOutputs,
    )
}
