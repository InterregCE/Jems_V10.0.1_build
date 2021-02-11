package io.cloudflight.jems.server.programme.service.language.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

data class AvailableProgrammeLanguages(
    val inputLanguages: Set<SystemLanguage>,
    val systemLanguages: Set<SystemLanguage>,
    val fallbackLanguage: SystemLanguage,
)
