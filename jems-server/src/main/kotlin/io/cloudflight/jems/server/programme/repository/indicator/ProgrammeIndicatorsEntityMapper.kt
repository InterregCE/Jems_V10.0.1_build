package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorTranslEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorTranslEntity
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import org.springframework.data.domain.Page

fun OutputIndicatorEntity.getName() = translatedValues.mapTo(HashSet()) {
    InputTranslation(it.translationId.language, it.name)
}

fun OutputIndicatorEntity.getMeasurementUnit() = translatedValues.mapTo(HashSet()) {
    InputTranslation(it.translationId.language, it.measurementUnit)
}

fun Page<OutputIndicatorEntity>.toOutputIndicatorDetailPage() = map { it.toOutputIndicatorDetail() }
fun OutputIndicatorEntity.toOutputIndicatorDetail() =
    OutputIndicatorDetail(
        id = id,
        identifier = identifier,
        code = code,
        name = getName(),
        programmeObjectivePolicy = programmePriorityPolicyEntity?.programmeObjectivePolicy,
        programmePriorityPolicyCode = programmePriorityPolicyEntity?.code,
        programmePriorityCode = programmePriorityPolicyEntity?.programmePriority?.code,
        measurementUnit = getMeasurementUnit(),
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
    programmePriorityPolicyEntity = programmePriorityPolicy,
    resultIndicatorEntity = resultIndicatorEntityReference,
    milestone = milestone,
    finalTarget = finalTarget,
    translatedValues = mutableSetOf()
).apply {
    translatedValues.addAll(
        name.filter { !it.translation.isNullOrBlank() }.plus(measurementUnit.filter { !it.translation.isNullOrBlank() })
            .mapTo(HashSet()) { it.language }
            .map { language ->
                OutputIndicatorTranslEntity(
                    translationId = TranslationId(
                        this,
                        language
                    ),
                    name = name.firstOrNull { it.language == language }?.translation,
                    measurementUnit = measurementUnit.firstOrNull { it.language == language }?.translation
                )
            }.toMutableSet()
    )
}

fun Iterable<OutputIndicatorEntity>.toOutputIndicatorSummaryList() = map { it.toOutputIndicatorSummary() }.toList()
fun Iterable<OutputIndicatorEntity>.toOutputIndicatorSummarySet() = map { it.toOutputIndicatorSummary() }.toSet()
fun OutputIndicatorEntity.toOutputIndicatorSummary() =
    OutputIndicatorSummary(
        id = id,
        identifier = identifier,
        code = code,
        name = getName(),
        programmePriorityCode = programmePriorityPolicyEntity?.programmePriority?.code,
        measurementUnit = getMeasurementUnit()
    )

fun ResultIndicator.toResultIndicatorEntity(
    programmePriorityPolicy: ProgrammeSpecificObjectiveEntity?,
    uniqueIdentifier: String? = null
) =
    ResultIndicatorEntity(
        id = id ?: 0,
        identifier = uniqueIdentifier ?: identifier,
        code = code,
        programmePriorityPolicyEntity = programmePriorityPolicy,
        baseline = baseline,
        referenceYear = referenceYear,
        finalTarget = finalTarget,
        comment = comment,
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addAll(
            name.filter { !it.translation.isNullOrBlank() }.plus(measurementUnit.filter { !it.translation.isNullOrBlank() }).plus(sourceOfData.filter { !it.translation.isNullOrBlank() })
                .mapTo(HashSet()) { it.language }
                .map { language ->
                    ResultIndicatorTranslEntity(
                        translationId = TranslationId(
                            this,
                            language
                        ),
                        name = name.firstOrNull { it.language == language }?.translation,
                        measurementUnit = measurementUnit.firstOrNull { it.language == language }?.translation,
                        sourceOfData = sourceOfData.firstOrNull { it.language == language }?.translation,
                    )
                }.toMutableSet()
        )
    }

fun ResultIndicatorEntity.getName() = translatedValues.mapTo(HashSet()) {
    InputTranslation(it.translationId.language, it.name)
}

fun ResultIndicatorEntity.getMeasurementUnit() = translatedValues.mapTo(HashSet()) {
    InputTranslation(it.translationId.language, it.measurementUnit)
}

fun ResultIndicatorEntity.getSourceOfData() = translatedValues.mapTo(HashSet()) {
    InputTranslation(it.translationId.language, it.sourceOfData)
}

fun Page<ResultIndicatorEntity>.toResultIndicatorDetailPage() = map { it.toResultIndicatorDetail() }
fun ResultIndicatorEntity.toResultIndicatorDetail() =
    ResultIndicatorDetail(
        id = id,
        identifier = identifier,
        code = code,
        name = getName(),
        programmeObjectivePolicy = programmePriorityPolicyEntity?.programmeObjectivePolicy,
        programmePriorityPolicyCode = programmePriorityPolicyEntity?.code,
        programmePriorityCode = programmePriorityPolicyEntity?.programmePriority?.code,
        measurementUnit = getMeasurementUnit(),
        baseline = baseline,
        referenceYear = referenceYear,
        finalTarget = finalTarget,
        sourceOfData = getSourceOfData(),
        comment = comment
    )


fun Iterable<ResultIndicatorEntity>.toResultIndicatorSummarySet() = map { it.toResultIndicatorSummary() }.toSet()
fun Iterable<ResultIndicatorEntity>.toResultIndicatorSummaryList() = map { it.toResultIndicatorSummary() }.toList()
fun ResultIndicatorEntity.toResultIndicatorSummary() =
    ResultIndicatorSummary(
        id = id,
        identifier = identifier,
        code = code,
        name = getName(),
        programmePriorityCode = programmePriorityPolicyEntity?.programmePriority?.code,
        measurementUnit = getMeasurementUnit(),
    )
