package io.cloudflight.jems.api.programme.dto

import io.cloudflight.jems.api.programme.SystemLanguage

data class SystemLanguageSelection (
    val name: SystemLanguage,
    val translationKey: String,
    val selected: Boolean
)
