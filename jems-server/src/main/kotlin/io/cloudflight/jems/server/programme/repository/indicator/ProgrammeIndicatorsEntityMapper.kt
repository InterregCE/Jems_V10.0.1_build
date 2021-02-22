package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import org.springframework.data.domain.Page

fun Page<OutputIndicatorEntity>.toOutputIndicatorDetailPage() = map { it.toOutputIndicatorDetail() }
fun OutputIndicatorEntity.toOutputIndicatorDetail() =
    OutputIndicatorDetail(
        id = id,
        identifier = identifier,
        code = code,
        name = name,
        programmeObjectivePolicy = programmePriorityPolicyEntity?.programmeObjectivePolicy,
        programmePriorityPolicyCode = programmePriorityPolicyEntity?.code,
        programmePriorityCode = programmePriorityPolicyEntity?.programmePriority?.code,
        measurementUnit = measurementUnit,
        milestone = milestone,
        finalTarget = finalTarget,
        resultIndicatorDetail = resultIndicatorEntity?.toResultIndicatorDetail(),
    )

fun OutputIndicator.toOutputIndicatorEntity(
    programmePriorityPolicy: ProgrammeSpecificObjectiveEntity?,
    resultIndicatorEntityReference: ResultIndicatorEntity?,
) = OutputIndicatorEntity(
    id = id ?: 0,
    identifier = identifier,
    code = code,
    name = name,
    programmePriorityPolicyEntity = programmePriorityPolicy,
    resultIndicatorEntity = resultIndicatorEntityReference,
    measurementUnit = measurementUnit,
    milestone = milestone,
    finalTarget = finalTarget
)

fun Iterable<OutputIndicatorEntity>.toOutputIndicatorSummaryList() = map { it.toOutputIndicatorSummary() }.toList()
fun Iterable<OutputIndicatorEntity>.toOutputIndicatorSummarySet() = map { it.toOutputIndicatorSummary() }.toSet()
fun OutputIndicatorEntity.toOutputIndicatorSummary() =
    OutputIndicatorSummary(
        id = id,
        identifier = identifier,
        code = code,
        name = name,
        programmePriorityCode = programmePriorityPolicyEntity?.programmePriority?.code,
        measurementUnit = measurementUnit,
    )

fun ResultIndicator.toResultIndicatorEntity(
    programmePriorityPolicy: ProgrammeSpecificObjectiveEntity?,
    uniqueIdentifier: String? = null
) =
    ResultIndicatorEntity(
        id = id ?: 0,
        identifier = uniqueIdentifier ?: identifier,
        code = code,
        name = name,
        programmePriorityPolicyEntity = programmePriorityPolicy,
        measurementUnit = measurementUnit,
        baseline = baseline,
        referenceYear = referenceYear,
        finalTarget = finalTarget,
        sourceOfData = sourceOfData,
        comment = comment
    )

fun Page<ResultIndicatorEntity>.toResultIndicatorDetailPage() = map { it.toResultIndicatorDetail() }
fun ResultIndicatorEntity.toResultIndicatorDetail() =
    ResultIndicatorDetail(
        id = id,
        identifier = identifier,
        code = code,
        name = name,
        programmeObjectivePolicy = programmePriorityPolicyEntity?.programmeObjectivePolicy,
        programmePriorityPolicyCode = programmePriorityPolicyEntity?.code,
        programmePriorityCode = programmePriorityPolicyEntity?.programmePriority?.code,
        measurementUnit = measurementUnit,
        baseline = baseline,
        referenceYear = referenceYear,
        finalTarget = finalTarget,
        sourceOfData = sourceOfData,
        comment = comment
    )


fun Iterable<ResultIndicatorEntity>.toResultIndicatorSummarySet() = map { it.toResultIndicatorSummary() }.toSet()
fun Iterable<ResultIndicatorEntity>.toResultIndicatorSummaryList() = map { it.toResultIndicatorSummary() }.toList()
fun ResultIndicatorEntity.toResultIndicatorSummary() =
    ResultIndicatorSummary(
        id = id,
        identifier = identifier,
        code = code,
        name = name,
        programmePriorityCode = programmePriorityPolicyEntity?.programmePriority?.code,
        measurementUnit = measurementUnit,
    )
