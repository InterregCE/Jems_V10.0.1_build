package io.cloudflight.jems.api.programme.dto

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

data class SystemLanguageSelection(
    val name: SystemLanguage,
    val translationKey: String,
    val selected: Boolean
)
