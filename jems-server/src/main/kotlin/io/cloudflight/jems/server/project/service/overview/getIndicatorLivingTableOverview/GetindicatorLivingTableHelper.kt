package io.cloudflight.jems.server.project.service.overview.getIndicatorLivingTableOverview

import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.result.model.OutputRow
import io.cloudflight.jems.server.project.service.result.model.OutputRowWithCurrent
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.result.model.ProjectResultWithCurrent
import java.math.BigDecimal

fun List<OutputRow>.toOutputModelWithCurrent() = map {
    OutputRowWithCurrent(
        workPackageId = it.workPackageId,
        workPackageNumber = it.workPackageNumber,
        outputTitle = it.outputTitle,
        outputNumber = it.outputNumber,
        outputTargetValue = it.outputTargetValue,
        indicatorOutputId = it.programmeOutputId,
        indicatorResultId = it.programmeResultId,
        deactivated = it.deactivated,
        measurementUnit = emptySet(),
        current = BigDecimal.ZERO,
    )
}

fun List<ProjectResult>.toResultModelWithCurrent() = map {
    ProjectResultWithCurrent(
        resultNumber = it.resultNumber,
        programmeResultIndicatorId = it.programmeResultIndicatorId,
        programmeResultIndicatorIdentifier = it.programmeResultIndicatorIdentifier,
        programmeResultName = it.programmeResultName,
        programmeResultMeasurementUnit = it.programmeResultMeasurementUnit,
        baseline = it.baseline,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        periodStartMonth = it.periodStartMonth,
        periodEndMonth = it.periodEndMonth,
        description = it.description,
        deactivated = it.deactivated,
        current = BigDecimal.ZERO,
    )
}

fun List<ProjectReportOutputLineOverview>.groupByOutputNumbers() = groupBy { it.workPackageNumber }
    .mapValues { (_, wpOutputs) ->
        wpOutputs.groupBy { it.number }
    }

fun List<ProjectReportProjectResult>.groupByResultNumbers() = groupBy { it.resultNumber }

fun Map<Int, Map<Int, List<ProjectReportOutputLineOverview>>>.getSumOfCurrentFor(workPackageNr: Int, outputNr: Int) =
    this.get(workPackageNr)?.get(outputNr)?.sumOf { it.currentReport } ?: BigDecimal.ZERO

fun Map<Int, List<ProjectReportProjectResult>>.getSumOfCurrentFor(resultNr: Int) =
    this[resultNr]?.sumOf { it.currentReport } ?: BigDecimal.ZERO

fun List<OutputRowWithCurrent>.fillInCurrentOutputValues(
    valuesByWpAndOutput: Map<Int, Map<Int, List<ProjectReportOutputLineOverview>>>,
) = forEach {
    it.current = valuesByWpAndOutput.getSumOfCurrentFor(it.workPackageNumber, it.outputNumber)
}

fun List<ProjectResultWithCurrent>.fillInCurrentResultValues(
    valuesByResultNr: Map<Int, List<ProjectReportProjectResult>>,
) = forEach {
    it.current = valuesByResultNr.getSumOfCurrentFor(it.resultNumber)
}
