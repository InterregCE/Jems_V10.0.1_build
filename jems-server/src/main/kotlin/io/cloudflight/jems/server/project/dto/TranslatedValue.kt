package io.cloudflight.jems.server.project.dto

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

/**
 * General translation object.
 */
interface TranslatedValue {
    val language: SystemLanguage

    fun isEmpty(): Boolean
}
