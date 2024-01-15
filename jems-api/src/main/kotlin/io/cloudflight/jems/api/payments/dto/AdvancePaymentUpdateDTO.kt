package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentUpdateDTO(
    val id: Long?,
    val projectId: Long,
    val partnerId: Long,

    val programmeFundId: Long? = null,
    val partnerContributionId: Long? = null,
    val partnerContributionSpfId: Long? = null,

    val amountPaid: BigDecimal?,
    val paymentDate: LocalDate? = null,
    val comment: String?,

    val paymentSettlements: List<AdvancePaymentSettlementDTO>,
)
