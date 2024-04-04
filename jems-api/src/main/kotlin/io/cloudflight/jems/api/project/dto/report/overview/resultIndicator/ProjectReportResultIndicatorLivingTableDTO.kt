package io.cloudflight.jems.api.project.dto.report.overview.resultIndicator

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectReportResultIndicatorLivingTableDTO(
    val id: Long?,
    val identifier: String?,
    val name: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val baselineIndicator: BigDecimal,
    val baselines: List<BigDecimal>,
    val targetValue: BigDecimal,
    val totalSubmitted: BigDecimal,

    val outputIndicators: List<ProjectReportOutputIndicatorLivingTableDTO>,
    val results: List<ProjectResultWithCurrentDTO>,
)
