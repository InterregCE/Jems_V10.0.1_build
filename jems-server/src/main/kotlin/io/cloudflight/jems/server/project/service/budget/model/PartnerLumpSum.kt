package io.cloudflight.jems.server.project.service.budget.model

import java.math.BigDecimal

data class PartnerLumpSum (
    val period: Int? = null,
    val amount: BigDecimal
)
