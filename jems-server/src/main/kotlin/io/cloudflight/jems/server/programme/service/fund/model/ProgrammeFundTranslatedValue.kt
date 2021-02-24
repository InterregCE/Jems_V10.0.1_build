package io.cloudflight.jems.server.programme.service.fund.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.project.dto.TranslatedValue

data class ProgrammeFundTranslatedValue(
    override val language: SystemLanguage,
    val abbreviation: String? = null,
    val description: String? = null,
): TranslatedValue {
    override fun isEmpty() = description.isNullOrBlank() && abbreviation.isNullOrBlank()
}
