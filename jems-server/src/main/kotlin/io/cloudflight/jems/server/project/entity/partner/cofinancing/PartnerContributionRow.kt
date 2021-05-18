package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus
import java.math.BigDecimal

interface PartnerContributionRow {
    val id: Long
    val partnerId: Long
    val name: String?
    val status: ProjectPartnerContributionStatus?
    val amount: BigDecimal
}