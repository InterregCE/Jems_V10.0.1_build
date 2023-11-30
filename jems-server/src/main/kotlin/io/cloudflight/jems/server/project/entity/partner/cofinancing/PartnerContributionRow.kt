package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal

interface PartnerContributionRow {
    val id: Long
    val name: String?
    val status: ProjectPartnerContributionStatus?
    val amount: BigDecimal
}
