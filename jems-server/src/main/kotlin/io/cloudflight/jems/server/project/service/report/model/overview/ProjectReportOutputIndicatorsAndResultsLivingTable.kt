package io.cloudflight.jems.server.project.service.report.model.overview

import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.result.model.OutputRowWithCurrent
import io.cloudflight.jems.server.project.service.result.model.ProjectResultWithCurrent

data class ProjectReportOutputIndicatorsAndResultsLivingTable(
    val outputIndicators: Map<ProjectReportOutputIndicatorOverview, List<OutputRowWithCurrent>>,
    val results: List<ProjectResultWithCurrent>,
)
