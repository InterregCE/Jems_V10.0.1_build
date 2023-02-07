package io.cloudflight.jems.server.project.service.workpackage.output.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class WorkPackageOutput(
    val workPackageId: Long,
    var outputNumber: Int = 0,
    var programmeOutputIndicatorId: Long? = null,
    val programmeOutputIndicatorIdentifier: String? = null,
    val programmeOutputIndicatorName: Set<InputTranslation> = emptySet(),
    val programmeOutputIndicatorMeasurementUnit: Set<InputTranslation> = emptySet(),
    var targetValue: BigDecimal? = null,
    var periodNumber: Int? = null,
    val periodStartMonth: Int? = null,
    val periodEndMonth: Int? = null,
    var title: Set<InputTranslation> = emptySet(),
    var description: Set<InputTranslation> =  emptySet(),
    var deactivated: Boolean,
)
