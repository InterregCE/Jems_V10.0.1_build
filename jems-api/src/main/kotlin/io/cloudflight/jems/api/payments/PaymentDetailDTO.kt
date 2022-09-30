package io.cloudflight.jems.api.payments

import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentDetailDTO(
    val id: Long,
    val paymentType: PaymentTypeDTO,
    val fundName: String,
    val projectCustomIdentifier: String,
    val projectAcronym: String,

    val amountApprovedPerFund: BigDecimal,
    val dateOfLastPayment: ZonedDateTime? = null,

    val partnerPayments: List<PaymentPartnerDTO>
)
