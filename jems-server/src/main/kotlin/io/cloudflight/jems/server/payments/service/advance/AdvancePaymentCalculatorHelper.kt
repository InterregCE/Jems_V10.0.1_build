package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import java.math.RoundingMode

fun AdvancePayment.calculateAmountSettled() = this.apply {
    this.amountSettled = this.paymentSettlements.sumOf { it.amountSettled }.setScale(2, RoundingMode.DOWN)
}