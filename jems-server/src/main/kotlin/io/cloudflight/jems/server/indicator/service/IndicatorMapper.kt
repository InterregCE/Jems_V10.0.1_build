package io.cloudflight.jems.server.indicator.service

import io.cloudflight.jems.api.indicator.dto.InputIndicatorOutputCreate
import io.cloudflight.jems.api.indicator.dto.InputIndicatorOutputUpdate
import io.cloudflight.jems.api.indicator.dto.InputIndicatorResultCreate
import io.cloudflight.jems.api.indicator.dto.InputIndicatorResultUpdate
import io.cloudflight.jems.api.indicator.dto.OutputIndicatorOutput
import io.cloudflight.jems.api.indicator.dto.OutputIndicatorResult
import io.cloudflight.jems.server.indicator.entity.IndicatorOutput
import io.cloudflight.jems.server.indicator.entity.IndicatorResult
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy

//region INDICATOR OUTPUT

fun IndicatorOutput.toOutputIndicator() = OutputIndicatorOutput(
    id = id,
    identifier = identifier,
    code = code,
    name = name,
    programmePriorityPolicySpecificObjective = programmePriorityPolicy?.programmeObjectivePolicy,
    programmePriorityPolicyCode = programmePriorityPolicy?.code,
    programmePriorityCode = programmePriorityPolicy?.programmePriority?.code,
    measurementUnit = measurementUnit,
    milestone = milestone,
    finalTarget = finalTarget
)

fun InputIndicatorOutputCreate.toEntity(programmePriorityPolicy: ProgrammePriorityPolicy?) = IndicatorOutput(
    identifier = identifier!!,
    code = code,
    name = name!!,
    programmePriorityPolicy = programmePriorityPolicy,
    measurementUnit = measurementUnit,
    milestone = milestone,
    finalTarget = finalTarget
)

fun InputIndicatorOutputUpdate.toEntity(
    uniqueIdentifier: String,
    programmePriorityPolicy: ProgrammePriorityPolicy?
) = IndicatorOutput(
    id = id,
    identifier = uniqueIdentifier,
    code = code,
    name = name!!,
    programmePriorityPolicy = programmePriorityPolicy,
    measurementUnit = measurementUnit,
    milestone = milestone,
    finalTarget = finalTarget
)
//endregion

//region INDICATOR RESULT

fun IndicatorResult.toOutputIndicator() = OutputIndicatorResult(
    id = id,
    identifier = identifier,
    code = code,
    name = name,
    programmePriorityPolicySpecificObjective = programmePriorityPolicy?.programmeObjectivePolicy,
    programmePriorityPolicyCode = programmePriorityPolicy?.code,
    programmePriorityCode = programmePriorityPolicy?.programmePriority?.code,
    measurementUnit = measurementUnit,
    baseline = baseline,
    referenceYear = referenceYear,
    finalTarget = finalTarget,
    sourceOfData = sourceOfData,
    comment = comment
)

fun InputIndicatorResultCreate.toEntity(programmePriorityPolicy: ProgrammePriorityPolicy?) = IndicatorResult(
    identifier = identifier!!,
    code = code,
    name = name!!,
    programmePriorityPolicy = programmePriorityPolicy,
    measurementUnit = measurementUnit,
    baseline = baseline,
    referenceYear = referenceYear,
    finalTarget = finalTarget,
    sourceOfData = sourceOfData,
    comment = comment
)

fun InputIndicatorResultUpdate.toEntity(
    uniqueIdentifier: String,
    programmePriorityPolicy: ProgrammePriorityPolicy?
) = IndicatorResult(
    id = id,
    identifier = uniqueIdentifier,
    code = code,
    name = name!!,
    programmePriorityPolicy = programmePriorityPolicy,
    measurementUnit = measurementUnit,
    baseline = baseline,
    referenceYear = referenceYear,
    finalTarget = finalTarget,
    sourceOfData = sourceOfData,
    comment = comment
)
//endregion
