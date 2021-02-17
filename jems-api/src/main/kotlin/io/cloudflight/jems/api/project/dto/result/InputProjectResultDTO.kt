package io.cloudflight.jems.api.project.dto.result

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class InputProjectResultDTO(
    val programmeResultIndicatorId: Long? = null,
    val targetValue: BigDecimal? = null,
    val periodNumber: Int? = null,
    val description: Set<InputTranslation> = emptySet()
)
