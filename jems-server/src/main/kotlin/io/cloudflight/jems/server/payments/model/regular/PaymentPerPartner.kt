package io.cloudflight.jems.server.payments.model.regular

import java.math.BigDecimal

data class PaymentPerPartner(
    val projectId: Long,
    val partnerId: Long,
    val orderNr: Int,
    val programmeLumpSumId: Long,
    val programmeFundId: Long,
    val amountApprovedPerPartner: BigDecimal,
)
