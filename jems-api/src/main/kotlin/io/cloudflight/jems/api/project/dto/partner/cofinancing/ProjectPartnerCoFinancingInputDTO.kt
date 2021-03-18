package io.cloudflight.jems.api.project.dto.partner.cofinancing

import java.math.BigDecimal

data class ProjectPartnerCoFinancingInputDTO(

    val fundType: ProjectPartnerCoFinancingFundType,
    val fundId: Long? = null,
    val percentage: BigDecimal? = null,

    )
