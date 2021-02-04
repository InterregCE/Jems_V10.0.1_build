package io.cloudflight.jems.server.project.service.workpackage.investment.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.project.dto.TranslatedValue

data class WorkPackageInvestmentTranslatedValue(
    override val language: SystemLanguage,
    val title: String? = null,
    var justificationExplanation: String? = null,
    var justificationTransactionalRelevance: String? = null,
    var justificationBenefits: String? = null,
    var justificationPilot: String? = null,
    var risk: String? = null,
    var documentation: String? = null,
    var ownershipSiteLocation: String? = null,
    var ownershipRetain: String? = null,
    var ownershipMaintenance: String? = null
) : TranslatedValue {
    override fun isEmpty() = title.isNullOrBlank() &&
            justificationExplanation.isNullOrBlank() &&
            justificationTransactionalRelevance.isNullOrBlank() &&
            justificationBenefits.isNullOrBlank() &&
            justificationPilot.isNullOrBlank() &&
            risk.isNullOrBlank() &&
            documentation.isNullOrBlank() &&
            ownershipSiteLocation.isNullOrBlank() &&
            ownershipRetain.isNullOrBlank() &&
            ownershipMaintenance.isNullOrBlank()
}
