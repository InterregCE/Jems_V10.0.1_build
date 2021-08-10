package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import java.math.BigDecimal

interface PartnerContributionRow {
    val id: Long
    val name: String?
    val status: ProjectPartnerContributionStatusDTO?
    val amount: BigDecimal
}
