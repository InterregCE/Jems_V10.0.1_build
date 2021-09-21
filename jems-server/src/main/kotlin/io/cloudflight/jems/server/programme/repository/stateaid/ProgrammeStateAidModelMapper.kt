package io.cloudflight.jems.server.programme.repository.stateaid

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.addTranslationEntities
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.common.entity.extractTranslation
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidTranslationEntity
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid

fun ProgrammeStateAidEntity.toModel() = ProgrammeStateAid(
    id = id,
    measure = measure,
    name = translatedValues.extractField { it.name },
    abbreviatedName = translatedValues.extractField { it.abbreviatedName },
    schemeNumber = schemeNumber,
    maxIntensity = maxIntensity,
    threshold = threshold,
    comments = translatedValues.extractField { it.comments }
)

fun Iterable<ProgrammeStateAidEntity>.toModel() = map { it.toModel() }.sortedBy { it.id }

fun Collection<ProgrammeStateAid>.toEntity() = map { model ->
    ProgrammeStateAidEntity(
        id = model.id,
        measure = model.measure,
        schemeNumber = model.schemeNumber,
        maxIntensity = model.maxIntensity,
        threshold = model.threshold,
        translatedValues = mutableSetOf()
    ).apply {
        translatedValues.addTranslationEntities(
            { language ->
                ProgrammeStateAidTranslationEntity(
                    translationId = TranslationId(this, language),
                    name = model.name.extractTranslation(language),
                    abbreviatedName = model.abbreviatedName.extractTranslation(language),
                    comments = model.comments.extractTranslation(language),
                )
            }, arrayOf(model.name, model.abbreviatedName, model.comments)
        )
    }
}
