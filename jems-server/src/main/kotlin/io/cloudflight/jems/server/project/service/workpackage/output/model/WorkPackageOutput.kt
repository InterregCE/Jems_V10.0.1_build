package io.cloudflight.jems.server.project.service.workpackage.output.model

import java.math.BigDecimal

data class WorkPackageOutput(
    val outputNumber: Int = 0,
    val programmeOutputIndicatorId: Long? = null,
    val programmeOutputIndicatorIdentifier: String? = null,
    val targetValue: BigDecimal? = null,
    val periodNumber: Int? = null,
    val translatedValues: Set<WorkPackageOutputTranslatedValue> = emptySet()
)
