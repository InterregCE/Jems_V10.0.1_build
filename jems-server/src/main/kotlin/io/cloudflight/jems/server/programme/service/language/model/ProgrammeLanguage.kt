package io.cloudflight.jems.server.programme.service.language.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

data class ProgrammeLanguage(
    val code: SystemLanguage,
    val ui: Boolean,
    val fallback: Boolean,
    val input: Boolean
)
