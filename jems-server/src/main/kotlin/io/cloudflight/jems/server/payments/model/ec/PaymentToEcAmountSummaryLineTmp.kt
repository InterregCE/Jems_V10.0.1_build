package io.cloudflight.jems.server.payments.model.ec

import java.math.BigDecimal

data class PaymentToEcAmountSummaryLineTmp(
    val priorityAxis: String?,
    val fundAmount: BigDecimal,
    val partnerContribution: BigDecimal,
    val ofWhichPublic: BigDecimal,
    val ofWhichAutoPublic: BigDecimal,
)
