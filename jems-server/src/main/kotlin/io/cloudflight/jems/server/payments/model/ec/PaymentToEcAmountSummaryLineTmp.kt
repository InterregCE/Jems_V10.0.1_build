package io.cloudflight.jems.server.payments.model.ec

import java.math.BigDecimal

data class PaymentToEcAmountSummaryLineTmp(
    val priorityId: Long?,
    val priorityAxis: String?,
    val fundAmount: BigDecimal,
    val partnerContribution: BigDecimal,
    val ofWhichPublic: BigDecimal,
    val ofWhichAutoPublic: BigDecimal,
    val unionContribution: BigDecimal,
    val correctedFundAmount: BigDecimal,
    val correctedTotalEligibleWithoutArt94Or95: BigDecimal,
)
