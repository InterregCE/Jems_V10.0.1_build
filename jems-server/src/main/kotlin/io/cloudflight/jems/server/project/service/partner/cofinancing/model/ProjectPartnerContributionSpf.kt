package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import java.math.BigDecimal

data class ProjectPartnerContributionSpf(

    // if updating
    val id: Long? = null,

    val name: String? = null,
    val status: ProjectPartnerContributionStatusDTO? = null,
    val amount: BigDecimal? = null
)
