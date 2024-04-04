package io.cloudflight.jems.server.project.controller.overview

import io.cloudflight.jems.api.project.dto.report.overview.resultIndicator.OutputRowWithCurrentDTO
import io.cloudflight.jems.api.project.dto.report.overview.resultIndicator.ProjectReportOutputIndicatorLivingTableDTO
import io.cloudflight.jems.api.project.dto.report.overview.resultIndicator.ProjectReportResultIndicatorLivingTableDTO
import io.cloudflight.jems.api.project.dto.report.overview.resultIndicator.ProjectResultWithCurrentDTO
import io.cloudflight.jems.server.project.service.report.model.overview.ProjectReportOutputIndicatorsAndResultsLivingTable
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import io.cloudflight.jems.server.project.service.result.model.OutputRowWithCurrent
import io.cloudflight.jems.server.project.service.result.model.ProjectResultWithCurrent

fun Map<ProjectReportResultIndicatorOverview, ProjectReportOutputIndicatorsAndResultsLivingTable>.toResultDto() =
    map { (resultIndicator, outputIndicatorsAndResults) ->
        ProjectReportResultIndicatorLivingTableDTO(
            id = resultIndicator.id,
            identifier = resultIndicator.identifier,
            name = resultIndicator.name,
            measurementUnit = resultIndicator.measurementUnit,
            baselineIndicator = resultIndicator.baselineIndicator,
            baselines = resultIndicator.baselines,
            targetValue = resultIndicator.targetValue,
            totalSubmitted = resultIndicator.currentReport,
            outputIndicators = outputIndicatorsAndResults.outputIndicators.toOutputDto(),
            results = outputIndicatorsAndResults.results.map { it.toDto() },
        )
    }.sortedBy { it.id ?: Long.MAX_VALUE }

fun Map<ProjectReportOutputIndicatorOverview, List<OutputRowWithCurrent>>.toOutputDto() = map { (outputIndicator, outputs) ->
    ProjectReportOutputIndicatorLivingTableDTO(
        id = outputIndicator.id,
        identifier = outputIndicator.identifier,
        name = outputIndicator.name,
        measurementUnit = outputIndicator.measurementUnit,
        targetValue = outputIndicator.targetValue,
        totalSubmitted = outputIndicator.currentReport,
        outputs = outputs.map { it.toDto() },
    )
}.sortedBy { it.id ?: Long.MAX_VALUE }

fun OutputRowWithCurrent.toDto() = OutputRowWithCurrentDTO(
    workPackageId = workPackageId,
    workPackageNumber = workPackageNumber,
    outputTitle = outputTitle,
    outputNumber = outputNumber,
    outputTargetValue = outputTargetValue,
    indicatorOutputId = indicatorOutputId,
    indicatorResultId = indicatorResultId,
    measurementUnit = measurementUnit,
    deactivated = deactivated,
    current = current,
)

fun ProjectResultWithCurrent.toDto() = ProjectResultWithCurrentDTO(
    resultNumber = resultNumber,
    programmeResultIndicatorId = programmeResultIndicatorId,
    programmeResultIndicatorIdentifier = programmeResultIndicatorIdentifier,
    programmeResultName = programmeResultName,
    programmeResultMeasurementUnit = programmeResultMeasurementUnit,
    baseline = baseline,
    targetValue = targetValue,
    periodNumber = periodNumber,
    periodStartMonth = periodStartMonth,
    periodEndMonth = periodEndMonth,
    description = description,
    deactivated = deactivated,
    current = current,
)
