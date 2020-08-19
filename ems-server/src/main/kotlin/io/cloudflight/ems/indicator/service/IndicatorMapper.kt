package io.cloudflight.ems.indicator.service

import io.cloudflight.ems.api.indicator.dto.InputIndicatorOutputCreate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorOutputUpdate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorResultCreate
import io.cloudflight.ems.api.indicator.dto.InputIndicatorResultUpdate
import io.cloudflight.ems.api.indicator.dto.OutputIndicatorOutput
import io.cloudflight.ems.api.indicator.dto.OutputIndicatorResult
import io.cloudflight.ems.indicator.entity.IndicatorOutput
import io.cloudflight.ems.indicator.entity.IndicatorResult
import io.cloudflight.ems.programme.entity.ProgrammePriorityPolicy

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

fun InputIndicatorOutputUpdate.toEntity(programmePriorityPolicy: ProgrammePriorityPolicy?) = IndicatorOutput(
    id = id,
    identifier = identifier!!,
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

fun InputIndicatorResultUpdate.toEntity(programmePriorityPolicy: ProgrammePriorityPolicy?) = IndicatorResult(
    id = id,
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
//endregion
