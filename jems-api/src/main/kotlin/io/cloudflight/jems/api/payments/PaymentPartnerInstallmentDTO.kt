package io.cloudflight.jems.api.payments

import io.cloudflight.jems.api.user.dto.OutputUser
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentPartnerInstallmentDTO(
    val id: Long? = null,
    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate?,
    val comment: String?,

    val savePaymentInfo: Boolean? = null,
    val savePaymentInfoUser: OutputUser? = null,
    val savePaymentDate: LocalDate? = null,
    val paymentConfirmed: Boolean? = null,
    val paymentConfirmedUser: OutputUser? = null,
    val paymentConfirmedDate: LocalDate? = null
)
