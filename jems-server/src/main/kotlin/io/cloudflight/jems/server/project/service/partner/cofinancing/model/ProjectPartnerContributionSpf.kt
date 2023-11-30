package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import java.math.BigDecimal

data class ProjectPartnerContributionSpf(

    // if updating
    override val id: Long? = null,

    override val name: String? = null,
    override val status: ProjectPartnerContributionStatus? = null,
    override val amount: BigDecimal? = null

): ProjectContribution {

    override fun isNotPartner() = name != null

}
