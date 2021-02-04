package io.cloudflight.jems.server.programme.controller.language

import io.cloudflight.jems.api.programme.dto.language.AvailableProgrammeLanguagesDTO
import io.cloudflight.jems.api.programme.dto.language.ProgrammeLanguageDTO
import io.cloudflight.jems.server.programme.service.language.model.AvailableProgrammeLanguages
import io.cloudflight.jems.server.programme.service.language.model.ProgrammeLanguage

fun Iterable<ProgrammeLanguageDTO>.toModel() = map {
    ProgrammeLanguage(
        code = it.code,
        ui = it.ui,
        fallback = it.fallback,
        input = it.input,
    )
}

fun Iterable<ProgrammeLanguage>.toDto() = map {
    ProgrammeLanguageDTO(
        code = it.code,
        ui = it.ui,
        fallback = it.fallback,
        input = it.input,
    )
}

fun AvailableProgrammeLanguages.toDto() = AvailableProgrammeLanguagesDTO(
    inputLanguages = inputLanguages.mapTo(HashSet()) { it.name },
    systemLanguages = systemLanguages.mapTo(HashSet()) { it.name },
    fallbackLanguage = fallbackLanguage.name,
)
