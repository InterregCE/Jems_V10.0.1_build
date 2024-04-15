package io.cloudflight.jems.api.project.dto.report.overview.resultIndicator

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectReportOutputIndicatorLivingTableDTO(
    val id: Long?,
    val identifier: String?,
    val name: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val targetValue: BigDecimal,
    val totalSubmitted: BigDecimal,
    val outputs: List<OutputRowWithCurrentDTO>
)
