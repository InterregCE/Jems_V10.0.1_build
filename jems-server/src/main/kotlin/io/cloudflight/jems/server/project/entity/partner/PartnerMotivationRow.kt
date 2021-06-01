package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.common.entity.TranslationView

interface PartnerMotivationRow: TranslationView {
    val partnerId: Long
    val organizationRelevance: String?
    val organizationRole: String?
    val organizationExperience: String?
}