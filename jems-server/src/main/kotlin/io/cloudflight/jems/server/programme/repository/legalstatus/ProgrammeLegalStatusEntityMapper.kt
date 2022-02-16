package io.cloudflight.jems.server.programme.repository.legalstatus

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusTranslationEntity
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus

fun Iterable<ProgrammeLegalStatusEntity>.toModel() = map { it.toModel() }

fun ProgrammeLegalStatusEntity.toModel() = ProgrammeLegalStatus(
    id = id,
    description = translatedValues.extractField { it.description },
    type = type
)

fun Collection<ProgrammeLegalStatus>.toEntity() = map { model ->
    ProgrammeLegalStatusEntity(
        id = model.id,
        translatedValues = mutableSetOf(),
        type = model.type
    ).apply {
        translatedValues.addAll(model.description.map { description ->
            ProgrammeLegalStatusTranslationEntity(
                translationId = TranslationId(this, language = description.language),
                description = description.translation,
            )
        })
    }
}
