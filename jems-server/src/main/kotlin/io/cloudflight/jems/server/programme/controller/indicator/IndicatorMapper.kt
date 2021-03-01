package io.cloudflight.jems.server.programme.controller.indicator

import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorDetailDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorSummaryDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorDetailDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorSummaryDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorUpdateRequestDTO
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import org.springframework.data.domain.Page

fun Page<OutputIndicatorDetail>.toOutputIndicatorDetailDTOPage() = map { it.toOutputIndicatorDetailDTO() }
fun OutputIndicatorDetail.toOutputIndicatorDetailDTO() = OutputIndicatorDetailDTO(
    id = id,
    identifier = identifier,
    code = code,
    name = name,
    programmePriorityPolicySpecificObjective = programmeObjectivePolicy,
    programmePriorityPolicyCode = programmePriorityPolicyCode,
    programmePriorityCode = programmePriorityCode,
    measurementUnit = measurementUnit,
    milestone = milestone,
    finalTarget = finalTarget,
    resultIndicatorId = resultIndicatorDetail?.id,
    resultIndicatorIdentifier = resultIndicatorDetail?.identifier
)

fun Set<OutputIndicatorSummary>.toOutputIndicatorSummaryDTOSet() = map { it.toOutputIndicatorSummaryDTO() }.toSet()
fun List<OutputIndicatorSummary>.toOutputIndicatorSummaryDTOList() = map { it.toOutputIndicatorSummaryDTO() }.toList()
fun OutputIndicatorSummary.toOutputIndicatorSummaryDTO() =
    OutputIndicatorSummaryDTO(
        id = id,
        identifier = identifier,
        code = code,
        name = name,
        measurementUnit = measurementUnit,
        programmePriorityCode = programmePriorityCode
    )

fun OutputIndicatorCreateRequestDTO.toOutputIndicator() =
    OutputIndicator(
        id = 0L,
        identifier = identifier!!,
        code = code,
        name = name,
        programmeObjectivePolicy = programmeObjectivePolicy,
        measurementUnit = measurementUnit,
        milestone = milestone,
        finalTarget = finalTarget,
        resultIndicatorId = resultIndicatorId
    )

fun OutputIndicatorUpdateRequestDTO.toOutputIndicator() =
    OutputIndicator(
        id = id,
        identifier = identifier!!,
        code = code,
        name = name,
        programmeObjectivePolicy = programmeObjectivePolicy,
        measurementUnit = measurementUnit,
        milestone = milestone,
        finalTarget = finalTarget,
        resultIndicatorId = resultIndicatorId
    )


fun Page<ResultIndicatorDetail>.toResultIndicatorDetailDTOPage() = map { it.toResultIndicatorDetailDTO() }
fun ResultIndicatorDetail.toResultIndicatorDetailDTO() = ResultIndicatorDetailDTO(
    id = id,
    identifier = identifier,
    code = code,
    name = name,
    programmePriorityPolicySpecificObjective = programmeObjectivePolicy,
    programmePriorityPolicyCode = programmePriorityPolicyCode,
    programmePriorityCode = programmePriorityCode,
    measurementUnit = measurementUnit,
    baseline = baseline,
    referenceYear = referenceYear,
    finalTarget = finalTarget,
    sourceOfData = sourceOfData,
    comment = comment
)

fun ResultIndicatorCreateRequestDTO.toResultIndicator() =
    ResultIndicator(
        id = 0L,
        identifier = identifier!!,
        code = code,
        name = name,
        programmeObjectivePolicy = programmeObjectivePolicy,
        measurementUnit = measurementUnit,
        baseline = baseline,
        referenceYear = referenceYear,
        finalTarget = finalTarget,
        sourceOfData = sourceOfData,
        comment = comment
    )

fun ResultIndicatorUpdateRequestDTO.toResultIndicator() =
    ResultIndicator(
        id = id,
        identifier = identifier!!,
        code = code,
        name = name,
        programmeObjectivePolicy = programmeObjectivePolicy,
        measurementUnit = measurementUnit,
        baseline = baseline,
        referenceYear = referenceYear,
        finalTarget = finalTarget,
        sourceOfData = sourceOfData,
        comment = comment
    )

fun Set<ResultIndicatorSummary>.toResultIndicatorSummaryDTOSet() = map { it.toResultIndicatorSummaryDTO() }.toSet()
fun List<ResultIndicatorSummary>.toResultIndicatorSummaryDTOList() = map { it.toResultIndicatorSummaryDTO() }.toList()
fun ResultIndicatorSummary.toResultIndicatorSummaryDTO() = ResultIndicatorSummaryDTO(
    id = id,
    identifier = identifier,
    code = code,
    name = name,
    programmePriorityCode = programmePriorityCode,
    measurementUnit = measurementUnit
)
