package io.cloudflight.jems.server.programme.repository.language

import io.cloudflight.jems.server.programme.entity.language.ProgrammeLanguageEntity
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage

fun Iterable<ProgrammeLanguage>.toEntity() = map {
    ProgrammeLanguageEntity(
        code = it.code,
        ui = it.ui,
        fallback = it.fallback,
        input = it.input,
    )
}

fun Iterable<ProgrammeLanguageEntity>.toModel() = map {
    ProgrammeLanguage(
        code = it.code,
        ui = it.ui,
        fallback = it.fallback,
        input = it.input,
    )
}
