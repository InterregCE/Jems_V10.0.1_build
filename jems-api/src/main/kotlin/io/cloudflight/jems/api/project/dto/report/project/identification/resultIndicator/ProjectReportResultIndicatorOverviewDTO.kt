package io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.project.projectResults.ProjectReportProjectResultDTO
import java.math.BigDecimal

data class ProjectReportResultIndicatorOverviewDTO(
    val id: Long?,
    val identifier: String?,
    val name: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val baselineIndicator: BigDecimal,
    val baselines: List<BigDecimal>,
    val targetValue: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,

    val outputIndicators: List<ProjectReportOutputIndicatorOverviewDTO>,
    val results: List<ProjectReportProjectResultDTO>,
)
