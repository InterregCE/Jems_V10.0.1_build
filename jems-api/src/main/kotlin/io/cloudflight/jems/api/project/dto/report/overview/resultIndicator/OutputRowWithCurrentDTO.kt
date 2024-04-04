package io.cloudflight.jems.api.project.dto.report.overview.resultIndicator

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class OutputRowWithCurrentDTO (
    val workPackageId: Long,
    val workPackageNumber: Int,
    val outputTitle: Set<InputTranslation>,
    val outputNumber: Int,
    val outputTargetValue: BigDecimal,
    val indicatorOutputId: Long?,
    val indicatorResultId: Long?,
    val measurementUnit: Set<InputTranslation>,
    val deactivated: Boolean,
    var current: BigDecimal,
)

