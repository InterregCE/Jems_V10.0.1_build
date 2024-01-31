package io.cloudflight.jems.api.payments.dto.account.finance.withdrawn

import java.math.BigDecimal

data class AmountWithdrawnPerPriorityDTO(
    val priorityAxis: String,
    val perYear: List<AmountWithdrawnPerYearDTO>,

    val withdrawalTotal: BigDecimal,
    val withdrawalPublic: BigDecimal,
)
