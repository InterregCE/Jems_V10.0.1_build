package io.cloudflight.jems.server.payments.model.regular

import io.cloudflight.jems.server.payments.entity.PaymentEntity
import java.math.BigDecimal
import java.time.LocalDate

data class PaymentToProjectTmp (
    val payment: PaymentEntity,
    val amountPaid: BigDecimal?,
    val amountAuthorized: BigDecimal?,
    val lastPaymentDate: LocalDate?,
    val totalEligibleForRegular: BigDecimal?
)
