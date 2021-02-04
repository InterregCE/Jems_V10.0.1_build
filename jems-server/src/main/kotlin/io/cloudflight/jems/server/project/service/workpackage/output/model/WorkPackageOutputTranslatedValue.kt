package io.cloudflight.jems.server.project.service.workpackage.output.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.project.dto.TranslatedValue

data class WorkPackageOutputTranslatedValue(
    override val language: SystemLanguage,
    val title: String? = null,
    val description: String? = null,
): TranslatedValue {
    override fun isEmpty() = title.isNullOrBlank() && description.isNullOrBlank()
}
