package io.cloudflight.jems.server.project.service.budget.model

import java.math.BigDecimal

data class ProjectPartnerCost(

    val partnerId: Long,
    val sum: BigDecimal

)
