package io.cloudflight.jems.server.payments.model.account

import java.math.BigDecimal

data class PaymentAccountAmountSummaryLineTmp(
    val priorityId: Long?,
    val priorityAxis: String?,
    val fundAmount: BigDecimal,
    val partnerContribution: BigDecimal,
    val ofWhichPublic: BigDecimal,
    val ofWhichAutoPublic: BigDecimal,
)
