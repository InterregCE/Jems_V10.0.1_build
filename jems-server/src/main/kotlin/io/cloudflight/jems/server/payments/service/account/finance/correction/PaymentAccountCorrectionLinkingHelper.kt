package io.cloudflight.jems.server.payments.service.account.finance.correction

import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.service.ecPayment.plusNullable

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


fun Map<Long?, PaymentAccountAmountSummaryLine>.plus(other: Map<Long?, PaymentAccountAmountSummaryLine>): Map<Long?, PaymentAccountAmountSummaryLine> {
    val priorityIds = keys union other.keys

    return priorityIds.associateWith { priorityId ->
        PaymentAccountAmountSummaryLine(
            priorityAxis = get(priorityId)?.priorityAxis ?: other[priorityId]?.priorityAxis,
            totalEligibleExpenditure = get(priorityId)?.totalEligibleExpenditure.plusNullable(other[priorityId]?.totalEligibleExpenditure),
            totalPublicContribution = get(priorityId)?.totalPublicContribution.plusNullable(other[priorityId]?.totalPublicContribution),
        )
    }
}


