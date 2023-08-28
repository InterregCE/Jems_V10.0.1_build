package io.cloudflight.jems.server.payments.service.advance

import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import java.math.RoundingMode

fun <C : Iterable<AdvancePayment>> C.calculateAmountSettled(): C = this.onEach {
    it.amountSettled = it.paymentSettlements.sumOf { it.amountSettled }.setScale(2, RoundingMode.DOWN)
}
