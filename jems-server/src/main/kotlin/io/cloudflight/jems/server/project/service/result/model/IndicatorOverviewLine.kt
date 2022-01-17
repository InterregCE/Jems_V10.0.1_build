package io.cloudflight.jems.server.project.service.result.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class IndicatorOverviewLine(
    val outputIndicator: IndicatorOutput?,
    val projectOutput: ProjectOutput?,
    val resultIndicator: IndicatorResult?,
    val onlyResultWithoutOutputs: Boolean,
)

data class IndicatorOutput(
    val id: Long,
    val identifier: String,
    val name: Set<InputTranslation>,
    val measurementUnit: Set<InputTranslation>,
    val targetValueSumUp: BigDecimal,
)

data class IndicatorResult(
    val id: Long,
    val identifier: String,
    val name: Set<InputTranslation> = emptySet(),
    val measurementUnit: Set<InputTranslation> = emptySet(),
    val baseline: Set<BigDecimal> = emptySet(),
    val targetValueSumUp: BigDecimal,
)

data class ProjectOutput(
    val projectOutputNumber: String,
    val projectOutputTitle: Set<InputTranslation>,
    val projectOutputTargetValue: BigDecimal,
)

data class OutputRow(
    val workPackageId: Long,
    val workPackageNumber: Int,
    val outputTitle: Set<InputTranslation>,
    val outputNumber: Int,
    val outputTargetValue: BigDecimal,
    val programmeOutputId: Long?,
    val programmeResultId: Long?,
)
