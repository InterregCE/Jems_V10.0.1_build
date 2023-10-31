package io.cloudflight.jems.server.payments.model.ec.overview

import java.math.BigDecimal

data class EcPaymentSummaryLine(
    val totalEligibleExpenditure: BigDecimal,
    val totalUnionContribution: BigDecimal,
    val totalPublicContribution: BigDecimal
)
