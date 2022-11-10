package io.cloudflight.jems.server.payments.service.model

import java.math.BigDecimal
import java.time.LocalDate

data class AdvancePaymentUpdate(
    val id: Long?,
    val projectId: Long,
    val partnerId: Long,

    val programmeFundId: Long? = null,
    val partnerContributionId: Long? = null,
    val partnerContributionSpfId: Long? = null,

    val amountAdvance: BigDecimal?,
    val dateOfPayment: LocalDate? = null,
    val comment: String?,

    val paymentAuthorized: Boolean? = null,
    var paymentAuthorizedUserId: Long? = null,
    var paymentAuthorizedDate: LocalDate? = null,
    val paymentConfirmed: Boolean? = null,
    var paymentConfirmedUserId: Long? = null,
    var paymentConfirmedDate: LocalDate? = null
)
