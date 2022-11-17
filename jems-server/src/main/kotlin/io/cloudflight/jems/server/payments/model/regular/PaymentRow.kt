package io.cloudflight.jems.server.payments.model.regular

import io.cloudflight.jems.server.payments.model.regular.PaymentPerPartner
import java.math.BigDecimal

interface PaymentRow {
    val projectId: Long
    val partnerId: Long
    val orderNr: Int
    val programmeLumpSumId: Long
    val programmeFundId: Long
    val partnerPayments: List<PaymentPerPartner>
    val amountApprovedPerPartner: BigDecimal
}
