package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal

interface PaymentRow {
    val projectId: Long
    val partnerId: Long
    val orderNr: Int
    val programmeLumpSumId: Long
    val programmeFundId: Long
    val amountApprovedPerFund: BigDecimal
}
