package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import java.math.BigDecimal

data class ProjectPartnerContributionSpf(

    // if updating
    override val id: Long? = null,

    override val name: String? = null,
    override val status: ProjectPartnerContributionStatusDTO? = null,
    override val amount: BigDecimal? = null

): ProjectContribution
