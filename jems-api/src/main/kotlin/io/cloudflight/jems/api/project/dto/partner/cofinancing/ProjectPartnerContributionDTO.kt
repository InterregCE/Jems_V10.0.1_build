package io.cloudflight.jems.api.project.dto.partner.cofinancing

import java.math.BigDecimal

data class ProjectPartnerContributionDTO(

    // if updating
    val id: Long? = null,

    val name: String? = null,
    val status: ProjectPartnerContributionStatusDTO? = null,
    val partner: Boolean,
    val amount: BigDecimal? = null

)
