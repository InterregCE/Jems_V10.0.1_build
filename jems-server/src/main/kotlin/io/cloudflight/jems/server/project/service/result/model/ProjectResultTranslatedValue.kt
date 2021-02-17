package io.cloudflight.jems.server.project.service.result.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.project.dto.TranslatedValue

data class ProjectResultTranslatedValue(
    override val language: SystemLanguage,
    val description: String? = null,
): TranslatedValue {
    override fun isEmpty() = description.isNullOrBlank()
}
