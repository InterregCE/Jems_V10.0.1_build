package io.cloudflight.jems.api.project.dto.report.project.identification.resultIndicator

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectReportOutputIndicatorOverviewDTO(
    val id: Long?,
    val identifier: String?,
    val name: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val targetValue: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentReport: BigDecimal,
    val outputs: List<ProjectReportOutputLineOverviewDTO>
)
