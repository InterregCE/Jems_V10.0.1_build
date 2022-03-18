package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal

data class PartnerBudgetPerFund(
    val fund: ProgrammeFund? = null,
    val percentage: BigDecimal,
    var percentageOfTotal: BigDecimal? = BigDecimal.ZERO,
    val value: BigDecimal,
)
