package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal

data class ProjectPartnerBudgetEntity(

    val partnerId: Long,
    val sum: BigDecimal

)
