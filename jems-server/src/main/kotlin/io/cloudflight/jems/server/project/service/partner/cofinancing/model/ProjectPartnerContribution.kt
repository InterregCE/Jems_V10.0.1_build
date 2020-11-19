package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus
import java.math.BigDecimal

data class ProjectPartnerContribution(

    // if updating
    val id: Long? = null,

    val name: String? = null,
    val status: ProjectPartnerContributionStatus? = null,
    val amount: BigDecimal? = null,
    val isPartner: Boolean,

) {
    fun isNotPartner() = !isPartner
}
