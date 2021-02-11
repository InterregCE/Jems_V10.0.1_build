package io.cloudflight.jems.api.programme.dto.language

data class ProgrammeLanguageDTO(
    val code: SystemLanguage,
    val ui: Boolean,
    val fallback: Boolean,
    val input: Boolean
)
