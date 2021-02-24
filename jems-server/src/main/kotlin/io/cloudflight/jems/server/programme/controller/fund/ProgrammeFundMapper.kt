package io.cloudflight.jems.server.programme.controller.fund

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundTranslatedValue
import io.cloudflight.jems.server.project.controller.workpackage.extractField
import io.cloudflight.jems.server.project.controller.workpackage.extractLanguages
import io.cloudflight.jems.server.project.controller.workpackage.groupByLanguage

fun ProgrammeFund.toDto() =
    ProgrammeFundDTO(
        id = id,
        selected = selected,
        abbreviation = translatedValues.extractField { it.abbreviation },
        description = translatedValues.extractField { it.description },
    )

fun Iterable<ProgrammeFund>.toDto() = map { it.toDto() }

fun Iterable<ProgrammeFundDTO>.toModel() = map {
    ProgrammeFund(
        id = it.id ?: 0,
        selected = it.selected,
        translatedValues = combineTranslations(it.abbreviation, it.description),
    )
}

private fun combineTranslations(
    abbreviation: Set<InputTranslation>,
    description: Set<InputTranslation>
): Set<ProgrammeFundTranslatedValue> {
    val abbreviationMap = abbreviation.groupByLanguage()
    val descriptionMap = description.groupByLanguage()

    return extractLanguages(abbreviationMap, descriptionMap)
        .map {
            ProgrammeFundTranslatedValue(
                language = it,
                abbreviation = abbreviationMap[it],
                description = descriptionMap[it],
            )
        }
        .filter { !it.isEmpty() }
        .toSet()
}
