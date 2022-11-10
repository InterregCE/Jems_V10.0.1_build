package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.common.entity.TranslationView
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

interface PartnerWithContributionsRow: TranslationView {
    val partnerId: Long
    val partnerAbbreviation: String
    val partnerRole: ProjectPartnerRole
    val partnerActive: Boolean
    val partnerSortNumber: Int

    val fundId: Long
    val fundAbbreviation: String

    val partnerContributionId: Long
    val partnerContributionName: String?
    val partnerContributionStatus: ProjectPartnerContributionStatusDTO
    val partnerContributionAmount: BigDecimal

    val partnerContributionSpfId: Long?
    val partnerContributionSpfName: String?
    val partnerContributionSpfStatus: ProjectPartnerContributionStatusDTO?
    val partnerContributionSpfAmount: BigDecimal?
}
