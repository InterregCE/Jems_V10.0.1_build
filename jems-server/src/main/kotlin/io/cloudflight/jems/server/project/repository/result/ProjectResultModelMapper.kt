package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorTranslEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.TranslationResultId
import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.project.entity.result.ProjectResultId
import io.cloudflight.jems.server.project.entity.result.ProjectResultRow
import io.cloudflight.jems.server.project.entity.result.ProjectResultTransl
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.result.model.ProjectResult

fun List<ProjectResult>.toIndexedEntity(
    projectId: Long,
    resolveProgrammeResultIndicatorEntity: (Long?) -> ResultIndicatorEntity?,
) = mapIndexed { index, projectResult ->
    val resultId = ProjectResultId(projectId = projectId, resultNumber = index.plus(1))
    ProjectResultEntity(
        resultId = resultId,
        translatedValues = combineDescriptionsToTranslations(resultId, projectResult.description),
        periodNumber = projectResult.periodNumber,
        programmeResultIndicatorEntity = resolveProgrammeResultIndicatorEntity.invoke(projectResult.programmeResultIndicatorId),
        baseline  = projectResult.baseline,
        targetValue = projectResult.targetValue,
    )
}.toSet()

fun combineDescriptionsToTranslations(
    resultId: ProjectResultId,
    description: Set<InputTranslation>
): Set<ProjectResultTransl> {
    val descriptionMap = description.associateBy( { it.language }, { it.translation } )
    val languages = descriptionMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectResultTransl(
            TranslationResultId(resultId, it),
            descriptionMap[it]
        )
    }
}

fun ProjectEntity.toResultModel() = results.sortedBy { it.resultId.resultNumber }.map {
    ProjectResult(
        resultNumber = it.resultId.resultNumber,
        programmeResultIndicatorId = it.programmeResultIndicatorEntity?.id,
        programmeResultIndicatorIdentifier = it.programmeResultIndicatorEntity?.identifier,
        programmeResultName = it.programmeResultIndicatorEntity?.translatedValues?.extractIndicatorField { it.name } ?: emptySet(),
        programmeResultMeasurementUnit = it.programmeResultIndicatorEntity?.translatedValues?.extractIndicatorField { it.measurementUnit } ?: emptySet(),
        baseline = it.baseline,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        periodStartMonth = periods.find { period -> period.id.number == it.periodNumber }?.start,
        periodEndMonth = periods.find { period -> period.id.number == it.periodNumber }?.end,
        description = it.translatedValues.extractField { projectResultTransl -> projectResultTransl.description },
    )
}

fun Set<ProjectResultTransl>.extractField(extractFunction: (ProjectResultTransl) -> String?) =
    map { InputTranslation(it.translationId.language, extractFunction.invoke(it)) }
        .filterTo(HashSet()) { !it.translation.isNullOrBlank() }

fun Set<ResultIndicatorTranslEntity>.extractIndicatorField(extractFunction: (ResultIndicatorTranslEntity) -> String?) =
    map { InputTranslation(it.translationId.language, extractFunction.invoke(it)) }
        .filterTo(HashSet()) { !it.translation.isNullOrBlank() }

fun List<ProjectResultRow>.toProjectResultHistoricalData(periods: Collection<ProjectPeriod>) =
    this.groupBy { it.resultNumber }.map { groupedRows ->
        ProjectResult(
            resultNumber = groupedRows.value.first().resultNumber,
            programmeResultIndicatorId = groupedRows.value.first().programmeResultIndicatorId,
            programmeResultIndicatorIdentifier = groupedRows.value.first().programmeResultIndicatorIdentifier,
            programmeResultName = groupedRows.value.extractField({ it.programmeResultIndicatorLanguage }) { it.programmeResultIndicatorName },
            programmeResultMeasurementUnit = groupedRows.value.extractField({ it.programmeResultIndicatorLanguage }) { it.programmeResultIndicatorMeasurementUnit },
            baseline = groupedRows.value.first().baseline,
            targetValue = groupedRows.value.first().targetValue,
            periodNumber = groupedRows.value.first().periodNumber,
            periodStartMonth = periods.find { period -> period.number == groupedRows.value.first().periodNumber }?.start,
            periodEndMonth = periods.find { period -> period.number == groupedRows.value.first().periodNumber }?.end,
            description = groupedRows.value.extractField { it.description }
        )
    }.sortedBy { it.resultNumber }
