package io.cloudflight.jems.server.project.controller.result

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.result.InputProjectResultDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.project.controller.workpackage.extractField
import io.cloudflight.jems.server.project.controller.workpackage.extractLanguages
import io.cloudflight.jems.server.project.controller.workpackage.groupByLanguage
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.result.model.ProjectResultTranslatedValue

fun List<InputProjectResultDTO>.toModel() = map {
    ProjectResult(
        programmeResultIndicatorId = it.programmeResultIndicatorId,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        translatedValues = combineDescriptions(it.description),
    )
}

fun combineDescriptions(
    description: Set<InputTranslation>
): Set<ProjectResultTranslatedValue> {
    val descriptionMap = description.groupByLanguage()

    return extractLanguages(descriptionMap)
        .map { ProjectResultTranslatedValue(language = it, description = descriptionMap[it]) }
        .filter { !it.isEmpty() }
        .toSet()
}

fun List<ProjectResult>.toDto() = map {
    ProjectResultDTO(
        resultNumber = it.resultNumber,
        programmeResultIndicatorId = it.programmeResultIndicatorId,
        programmeResultIndicatorIdentifier = it.programmeResultIndicatorIdentifier,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        description = it.translatedValues.extractField { it.description },
    )
}
