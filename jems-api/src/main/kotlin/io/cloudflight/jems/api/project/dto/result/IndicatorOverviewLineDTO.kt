package io.cloudflight.jems.api.project.dto.result

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class IndicatorOverviewLineDTO(
    val outputIndicatorId: Long?,
    val outputIndicatorIdentifier: String?,
    val outputIndicatorName: Set<InputTranslation>?,
    val outputIndicatorMeasurementUnit: Set<InputTranslation>?,
    val outputIndicatorTargetValueSumUp: BigDecimal?,

    val projectOutputNumber: String?,
    val projectOutputTitle: Set<InputTranslation>?,
    val projectOutputTargetValue: BigDecimal?,

    val resultIndicatorId: Long?,
    val resultIndicatorIdentifier: String?,
    val resultIndicatorName: Set<InputTranslation>?,
    val resultIndicatorMeasurementUnit: Set<InputTranslation>?,
    val resultIndicatorBaseline: Set<BigDecimal>?,
    val resultIndicatorTargetValueSumUp: BigDecimal?,

    val onlyResultWithoutOutputs: Boolean,
)
