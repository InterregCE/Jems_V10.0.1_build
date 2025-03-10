package io.cloudflight.jems.server.programme.repository.fund

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

fun ProgrammeFundEntity.toModel() = ProgrammeFund(
    id = id,
    selected = selected,
    type = type,
    abbreviation = translatedValues.extractField { it.abbreviation },
    description = translatedValues.extractField { it.description }
)

fun Iterable<ProgrammeFundEntity>.toModel() = map { it.toModel() }.sortedBy { it.id }

fun Collection<ProgrammeFund>.toEntity() = map { model ->
    model.toEntity()
}

fun ProgrammeFund.toEntity() =
    ProgrammeFundEntity(
        id = id, type = type, selected = selected, translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                ProgrammeFundTranslationEntity(
                    translationId = TranslationId(this, language),
                    abbreviation = abbreviation.extractTranslation(language),
                    description = description.extractTranslation(language),
                )
            }, arrayOf(abbreviation, description)
        )
    }




