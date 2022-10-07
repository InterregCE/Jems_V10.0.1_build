package io.cloudflight.jems.server.payments.service.model

import io.cloudflight.jems.api.user.dto.OutputUser
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentPartnerInstallment(
    val id: Long? = null,
    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate?,
    val comment: String? = null,

    val isSavePaymentInfo: Boolean? = null,
    val savePaymentInfoUser: OutputUser? = null,
    val savePaymentDate: LocalDate? = null,
    val isPaymentConfirmed: Boolean? = null,
    val paymentConfirmedUser: OutputUser? = null,
    val paymentConfirmedDate: LocalDate? = null
)
