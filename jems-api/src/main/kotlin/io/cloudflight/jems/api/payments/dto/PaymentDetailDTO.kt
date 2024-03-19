package io.cloudflight.jems.api.payments.dto

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.math.BigDecimal
import java.time.ZonedDateTime

data class PaymentDetailDTO(
    val id: Long,
    val paymentType: PaymentTypeDTO,
    val fund: ProgrammeFundDTO,
    val projectId: Long,
    val projectCustomIdentifier: String,
    val projectAcronym: String,
    val spf: Boolean,

    val amountApprovedPerFund: BigDecimal,
    val dateOfLastPayment: ZonedDateTime? = null,

    val partnerPayments: List<PaymentPartnerDTO>
)
