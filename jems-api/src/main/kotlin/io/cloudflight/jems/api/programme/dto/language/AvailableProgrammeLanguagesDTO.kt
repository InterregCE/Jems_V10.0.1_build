package io.cloudflight.jems.api.programme.dto.language

data class AvailableProgrammeLanguagesDTO (
    val inputLanguages: Set<String>,
    val systemLanguages: Set<String>,
    val fallbackLanguage: String,
)
