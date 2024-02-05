package io.cloudflight.jems.server.payments.service.account.corrections

import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLineTmp

fun Map<Long?, PaymentAccountAmountSummaryLineTmp>.sumUpProperColumns() =
    mapValues { (_, it) ->
        PaymentAccountAmountSummaryLine(
            priorityAxis = it.priorityAxis,
            totalEligibleExpenditure = it.fundAmount.plus(it.partnerContribution),
            totalPublicContribution = it.fundAmount.plus(it.ofWhichPublic).plus(it.ofWhichAutoPublic)
        )
    }

fun Collection<PaymentAccountAmountSummaryLine>.sumUp() = PaymentAccountAmountSummaryLine (
    priorityAxis = if ((allAxesSame())) firstOrNull()?.priorityAxis else null,
    totalEligibleExpenditure = sumOf { it.totalEligibleExpenditure },
    totalPublicContribution = sumOf { it.totalPublicContribution }
)
fun Collection<PaymentAccountAmountSummaryLine>.allAxesSame() = map{it.priorityAxis}.toSet().size == 1
