package io.cloudflight.jems.server.project.service.result.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectResult(
    val resultNumber: Int = 0, // this is computed automatically in persistence
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
    val deactivated: Boolean
)
