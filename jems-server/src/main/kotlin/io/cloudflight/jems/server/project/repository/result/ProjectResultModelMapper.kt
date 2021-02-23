package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.project.entity.TranslationResultId
import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.project.entity.result.ProjectResultId
import io.cloudflight.jems.server.project.entity.result.ProjectResultTransl
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.result.model.ProjectResultTranslatedValue
import kotlin.collections.HashSet

fun List<ProjectResult>.toIndexedEntity(
    projectId: Long,
    resolveProgrammeResultIndicatorEntity: (Long?) -> ResultIndicatorEntity?,
) = mapIndexed { index, projectResult ->
    val resultId = ProjectResultId(projectId = projectId, resultNumber = index.plus(1))
    ProjectResultEntity(
        resultId = resultId,
        translatedValues = projectResult.translatedValues.toEntity(resultId),
        periodNumber = projectResult.periodNumber,
        programmeResultIndicatorEntity = resolveProgrammeResultIndicatorEntity.invoke(projectResult.programmeResultIndicatorId),
        targetValue = projectResult.targetValue,
    )
}.toSet()

private fun Set<ProjectResultTranslatedValue>.toEntity(resultId: ProjectResultId) = mapTo(HashSet()) {
    ProjectResultTransl(
        translationId = TranslationResultId(resultId = resultId, it.language),
        description = it.description,
    )
}

fun Iterable<ProjectResultEntity>.toModel() = sortedBy { it.resultId.resultNumber }.map {
    ProjectResult(
        resultNumber = it.resultId.resultNumber,
        programmeResultIndicatorId = it.programmeResultIndicatorEntity?.id,
        programmeResultIndicatorIdentifier = it.programmeResultIndicatorEntity?.identifier,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        translatedValues = it.translatedValues.toModel(),
    )
}

private fun Set<ProjectResultTransl>.toModel() = mapTo(HashSet()) {
    ProjectResultTranslatedValue(
        language = it.translationId.language,
        description = it.description,
    )
}
