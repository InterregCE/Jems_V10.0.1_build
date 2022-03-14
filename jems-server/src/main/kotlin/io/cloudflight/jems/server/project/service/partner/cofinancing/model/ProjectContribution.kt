package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import java.math.BigDecimal

interface ProjectContribution {

    val id: Long?

    val name: String?

    val status: ProjectPartnerContributionStatusDTO?

    val amount: BigDecimal?

}
