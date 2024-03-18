package io.cloudflight.jems.api.payments.dto

import java.math.BigDecimal

data class PaymentToEcLinkingDTO(
    val payment: PaymentToProjectDTO,

    val paymentToEcId: Long?,
    val priorityAxis: String,

    val correctedTotalEligibleWithoutSco: BigDecimal,
    val correctedFundAmountUnionContribution: BigDecimal,
    val correctedFundAmountPublicContribution: BigDecimal,

    val partnerContribution: BigDecimal,

    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,

    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,

    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,

    val comment: String?,
)
