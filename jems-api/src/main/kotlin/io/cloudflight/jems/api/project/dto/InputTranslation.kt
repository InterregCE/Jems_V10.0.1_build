package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import javax.validation.constraints.Size

/**
 * General translation transfer object.
 */
data class InputTranslation(
    val language: SystemLanguage,
    val translation: String? = null
)
