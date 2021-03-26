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
    ProgrammeFundEntity(
        id = model.id, type = model.type, selected = model.selected, translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                ProgrammeFundTranslationEntity(
                    translationId = TranslationId(this, language),
                    abbreviation = model.abbreviation.extractTranslation(language),
                    description = model.description.extractTranslation(language),
                )
            }, arrayOf(model.abbreviation, model.description)
        )
    }
}

