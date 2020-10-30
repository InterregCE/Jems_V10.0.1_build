package io.cloudflight.jems.api.programme.dto

data class SystemLanguageSelection(
    val name: SystemLanguage,
    val translationKey: String,
    val selected: Boolean
)
