package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDeliverableDTO
import io.cloudflight.jems.server.project.dto.TranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverableTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityTranslatedValue

fun WorkPackageActivityDTO.toModel() = WorkPackageActivity(
    translatedValues = combineTranslations(title, description),
    startPeriod = startPeriod,
    endPeriod = endPeriod,
    deliverables = deliverables.toDeliverableModel(),
)

fun List<WorkPackageActivityDTO>.toModel() = map { it.toModel() }

fun WorkPackageActivityDeliverableDTO.toDeliverableModel() = WorkPackageActivityDeliverable(
    translatedValues = combineDeliverableTranslations(description),
    period = period,
)

fun List<WorkPackageActivityDeliverableDTO>.toDeliverableModel() = map { it.toDeliverableModel() }


fun WorkPackageActivity.toDto() = WorkPackageActivityDTO(
    title = translatedValues.extractField { it.title },
    startPeriod = startPeriod,
    endPeriod = endPeriod,
    description = translatedValues.extractField { it.description },
    deliverables = deliverables.toDeliverableDto(),
)

fun List<WorkPackageActivity>.toDto() = map { it.toDto() }

fun WorkPackageActivityDeliverable.toDeliverableDto() = WorkPackageActivityDeliverableDTO(
    description = translatedValues.extractField { it.description },
    period = period,
)

fun List<WorkPackageActivityDeliverable>.toDeliverableDto() = map { it.toDeliverableDto() }


fun combineTranslations(
    title: Set<InputTranslation>,
    description: Set<InputTranslation>
): Set<WorkPackageActivityTranslatedValue> {
    val titleMap = title.groupByLanguage()
    val descriptionMap = description.groupByLanguage()

    return extractLanguages(titleMap, descriptionMap)
        .map {
            WorkPackageActivityTranslatedValue(
                language = it,
                title = titleMap[it],
                description = descriptionMap[it],
            )
        }
        .filter { !it.isEmpty() }
        .toSet()
}

fun combineDeliverableTranslations(
    description: Set<InputTranslation>
): Set<WorkPackageActivityDeliverableTranslatedValue> {
    val descriptionMap = description.groupByLanguage()

    return extractLanguages(descriptionMap)
        .map {
            WorkPackageActivityDeliverableTranslatedValue(
                language = it,
                description = descriptionMap[it],
            )
        }
        .filter { !it.isEmpty() }
        .toSet()
}

fun extractLanguages(vararg data: Map<SystemLanguage, String?>): Set<SystemLanguage> =
    data.asIterable().map { it.keys }.reduce { first, second -> first union second }

fun Set<InputTranslation>.groupByLanguage() = associateBy({ it.language }, { it.translation })

inline fun <T : TranslatedValue> Set<T>.extractField(extractFunction: (T) -> String?) =
    map { InputTranslation(it.language, extractFunction.invoke(it)) }
        .filterTo(HashSet()) { !it.translation.isNullOrBlank() }
