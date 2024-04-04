package io.cloudflight.jems.api.project.dto.report.overview.resultIndicator

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectResultWithCurrentDTO(
    val resultNumber: Int,
    val programmeResultIndicatorId: Long? = null,
    val programmeResultIndicatorIdentifier: String? = null,
    val programmeResultName: Set<InputTranslation> = emptySet(),
    val programmeResultMeasurementUnit: Set<InputTranslation> = emptySet(),
    val baseline: BigDecimal,
    val targetValue: BigDecimal? = null,
    val periodNumber: Int? = null,
    val periodStartMonth: Int? = null,
    val periodEndMonth: Int? = null,
    val description: Set<InputTranslation> = emptySet(),
    val deactivated: Boolean,
    var current: BigDecimal,
)
