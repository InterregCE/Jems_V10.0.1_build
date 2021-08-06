package io.cloudflight.jems.server.programme.repository.stateaid

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidTranslationEntity
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid

fun Iterable<ProgrammeStateAidEntity>.toModel() = map {
    ProgrammeStateAid(
        id = it.id,
        measure = it.measure,
        name = it.translatedValues.extractField { it.name },
        abbreviatedName = it.translatedValues.extractField { it.abbreviatedName },
        schemeNumber = it.schemeNumber,
        maxIntensity = it.maxIntensity,
        threshold = it.threshold,
        comments = it.translatedValues.extractField { it.comments }
    )
}

fun Collection<ProgrammeStateAid>.toEntity() = map { model ->
    ProgrammeStateAidEntity(
        id = model.id,
        measure = model.measure,
        schemeNumber = model.schemeNumber,
        maxIntensity = model.maxIntensity,
        threshold = model.threshold,
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addAll(
            model.name.map { name ->
                ProgrammeStateAidTranslationEntity(
                    translationId = TranslationId(this, language = name.language),
                    name = name.translation
                )
            }
        )
        translatedValues.addAll(
            model.abbreviatedName.map { abbreviatedName ->
                ProgrammeStateAidTranslationEntity(
                    translationId = TranslationId(this, language = abbreviatedName.language),
                    abbreviatedName = abbreviatedName.translation
                )
            },
        )
        translatedValues.addAll(
            model.comments.map { comments ->
                ProgrammeStateAidTranslationEntity(
                    translationId = TranslationId(this, language = comments.language),
                    comments = comments.translation
                )
            },
        )
    }
}
