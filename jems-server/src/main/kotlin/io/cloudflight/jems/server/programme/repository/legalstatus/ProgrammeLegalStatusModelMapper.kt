package io.cloudflight.jems.server.programme.repository.legalstatus

import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusTranslationEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusTranslationId
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusTranslatedValue

fun Iterable<ProgrammeLegalStatusEntity>.toModel() = map {
    ProgrammeLegalStatus(
        id = it.id,
        translatedValues = it.translatedValues.toModel(),
    )
}

fun Set<ProgrammeLegalStatusTranslationEntity>.toModel() = mapTo(HashSet()) {
    ProgrammeLegalStatusTranslatedValue(
        language = it.translationId.language,
        description = it.description,
    )
}

fun Collection<ProgrammeLegalStatus>.toEntity() = map { model ->
    ProgrammeLegalStatusEntity(
        id = 0,
        translatedValues = mutableSetOf(),
    ).apply {
        this.translatedValues.addAll(model.translatedValues.map { transl ->
            ProgrammeLegalStatusTranslationEntity(
                translationId = ProgrammeLegalStatusTranslationId(legalStatus = this, language = transl.language),
                description = transl.description,
            )
        })
    }
}
