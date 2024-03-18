package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import java.math.BigDecimal

data class PaymentToEcPayment(
    val payment: PaymentToProject,

    val paymentToEcId: Long?,
    val priorityAxis: String,

    val partnerContribution: BigDecimal,

    val correctedTotalEligibleWithoutSco: BigDecimal,
    val correctedFundAmountUnionContribution: BigDecimal,
    val correctedFundAmountPublicContribution: BigDecimal,

    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,

    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,

    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,

    val comment: String?,
)
