package io.cloudflight.ems.api.programme.dto

import io.cloudflight.ems.api.programme.SystemLanguage

data class SystemLanguageSelection (
    val name: SystemLanguage,
    val translationKey: String,
    val selected: Boolean
)
