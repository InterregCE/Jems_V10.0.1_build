package io.cloudflight.jems.server.programme.service.legalstatus.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.project.dto.TranslatedValue

data class ProgrammeLegalStatusTranslatedValue(
    override val language: SystemLanguage,
    val description: String? = null,
): TranslatedValue {
    override fun isEmpty() = description.isNullOrBlank()
}
