package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.programme.entity.indicator.IndicatorResult
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.TranslationResultId
import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.project.entity.result.ProjectResultTransl
import java.util.UUID
import kotlin.collections.HashSet

fun ProjectResultDTO.toEntity(indicatorResult: IndicatorResult?, project: ProjectEntity, projectPeriod: ProjectPeriodEntity?) = ProjectResultEntity (
    project = project,
    resultNumber = resultNumber,
    programmeResultIndicator = indicatorResult,
    targetValue = targetValue,
    period = projectPeriod,
)

fun ProjectResultEntity.ProjectResultDTO() = ProjectResultDTO(
    resultNumber = resultNumber,
    programmeResultIndicatorId = programmeResultIndicator?.id,
    targetValue = targetValue,
    periodNumber = period?.id?.number,
    description = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) },
)

fun combineTranslatedValues(
    projectResultId: UUID,
    description: Set<InputTranslation>
): Set<ProjectResultTransl> {
    val descriptionMap = description.associateBy( { it.language }, { it.translation } )
    val languages = descriptionMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectResultTransl(
            TranslationResultId(projectResultId, it),
            descriptionMap[it]
        )
    }
}

fun Set<ProjectResultEntity>.toProjectResultSet() =
    this.map { it.ProjectResultDTO() }.sortedBy { it.resultNumber }.toSet()
