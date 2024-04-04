package io.cloudflight.jems.server.project.service.report.model.project.identification.overview

import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult

data class ProjectReportOutputIndicatorsAndResults(
    val outputIndicators: Map<ProjectReportOutputIndicatorOverview, List<ProjectReportOutputLineOverview>>,
    val results: List<ProjectReportProjectResult>,
)
