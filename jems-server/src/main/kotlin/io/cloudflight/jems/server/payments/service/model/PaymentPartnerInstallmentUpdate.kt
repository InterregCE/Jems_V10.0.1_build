package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal
import java.time.LocalDate

data class PaymentPartnerInstallmentUpdate(
    val id: Long? = null,

    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate?,
    val comment: String? = null,

    val isSavePaymentInfo: Boolean? = null,
    var savePaymentInfoUserId: Long? = null,
    var savePaymentDate: LocalDate? = null,
    val isPaymentConfirmed: Boolean? = null,
    var paymentConfirmedUserId: Long? = null,
    var paymentConfirmedDate: LocalDate? = null
)
