package io.cloudflight.jems.server.programme.repository.fund

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationId
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundTranslatedValue

fun ProgrammeFundEntity.toModel() = ProgrammeFund(
    id = id,
    selected = selected,
    translatedValues = translatedValues.toModel(),
)

fun Iterable<ProgrammeFundEntity>.toModel() = map { it.toModel() }

fun Set<ProgrammeFundTranslationEntity>.toModel() = mapTo(HashSet()) {
    ProgrammeFundTranslatedValue(
        language = it.translationId.language,
        abbreviation = it.abbreviation,
        description = it.description,
    )
}

fun Collection<ProgrammeFund>.toEntity() = map { model ->
    ProgrammeFundEntity(
        id = model.id,
        selected = model.selected,
        translatedValues = mutableSetOf(),
    ).apply {
        this.translatedValues.addAll(model.translatedValues.map { transl ->
            ProgrammeFundTranslationEntity(
                translationId = ProgrammeFundTranslationId(fund = this, language = transl.language),
                abbreviation = transl.abbreviation,
                description = transl.description,
            )
        })
    }
}
