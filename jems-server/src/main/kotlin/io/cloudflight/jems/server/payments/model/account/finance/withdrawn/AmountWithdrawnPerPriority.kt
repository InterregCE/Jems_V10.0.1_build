package io.cloudflight.jems.server.payments.model.account.finance.withdrawn

import java.math.BigDecimal

data class AmountWithdrawnPerPriority(
    val priorityAxis: String,
    val perYear: List<AmountWithdrawnPerYear>,

    var withdrawalTotal: BigDecimal,
    var withdrawalPublic: BigDecimal,
)
