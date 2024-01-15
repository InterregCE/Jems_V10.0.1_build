package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import java.math.BigDecimal

data class ProjectPartnerContribution(

    // if updating
    override val id: Long? = null,

    override val name: String? = null,
    override val status: ProjectPartnerContributionStatus? = null,
    override val amount: BigDecimal? = null,

    val isPartner: Boolean

): ProjectContribution {
    override fun isNotPartner() = !isPartner
}
