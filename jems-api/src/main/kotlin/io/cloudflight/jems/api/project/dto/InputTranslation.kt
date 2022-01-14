package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

/**
 * General translation transfer object.
 */
data class InputTranslation(
    val language: SystemLanguage,
    val translation: String? = null
){
    override fun toString(): String =
        "${this.language}=${this.translation}"
}
