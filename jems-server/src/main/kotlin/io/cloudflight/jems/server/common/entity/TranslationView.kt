package io.cloudflight.jems.server.common.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

interface TranslationView {
    val language: SystemLanguage?
}
