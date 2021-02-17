package io.cloudflight.jems.api.project.dto.workpackage.output

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class WorkPackageOutputDTO(
    val outputNumber: Int? = null,
    val programmeOutputIndicatorId: Long? = null,
    val programmeOutputIndicatorIdentifier: String? = null,
    val targetValue: BigDecimal? = null,
    val periodNumber: Int? = null,
    val title: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
)
