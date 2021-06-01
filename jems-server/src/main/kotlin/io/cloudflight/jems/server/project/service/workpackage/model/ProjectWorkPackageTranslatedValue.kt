package io.cloudflight.jems.server.project.service.workpackage.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.project.dto.TranslatedValue

data class ProjectWorkPackageTranslatedValue(
    override val language: SystemLanguage,
    val name: String? = null,
    val specificObjective: String? = null,
    val objectiveAndAudience: String? = null
): TranslatedValue {
    override fun isEmpty() = name.isNullOrBlank()
}
