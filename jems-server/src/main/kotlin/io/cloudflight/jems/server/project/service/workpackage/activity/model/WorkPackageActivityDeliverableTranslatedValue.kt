package io.cloudflight.jems.server.project.service.workpackage.activity.model

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import io.cloudflight.jems.server.project.dto.TranslatedValue

data class WorkPackageActivityDeliverableTranslatedValue(
    override val language: SystemLanguage,
    val description: String? = null,
): TranslatedValue {
    override fun isEmpty() = description.isNullOrBlank()
}
