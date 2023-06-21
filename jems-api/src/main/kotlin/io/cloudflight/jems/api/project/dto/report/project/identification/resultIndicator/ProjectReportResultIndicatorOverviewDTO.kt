package io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectReportResultIndicatorOverviewDTO(
    val id: Long?,
    val identifier: String?,
    val name: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val baseline: BigDecimal,
    val targetValue: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,
    val outputOverviews: List<ProjectReportOutputIndicatorOverviewDTO>
)
