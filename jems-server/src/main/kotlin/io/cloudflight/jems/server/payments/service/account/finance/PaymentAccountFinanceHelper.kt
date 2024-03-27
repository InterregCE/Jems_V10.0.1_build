package io.cloudflight.jems.server.payments.service.account.finance

import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import java.math.BigDecimal

fun Map<Long?, PaymentToEcAmountSummaryLine>.computeTotals() = mapValues { (_, it) ->
    PaymentAccountAmountSummaryLine(
        priorityAxis = it.priorityAxis,
        totalEligibleExpenditure = it.totalEligibleExpenditure + it.totalUnionContribution,
        totalPublicContribution = it.totalPublicContribution
    )
}

fun Map<PaymentToEcOverviewType, Map<Long?, PaymentToEcAmountSummaryLineTmp>>.sumUpProperColumns() =
    mapValues { (type, totals) -> totals.computeTotals(type) }

fun Map<Long?, PaymentToEcAmountSummaryLineTmp>.computeTotals(type: PaymentToEcOverviewType) =
    mapValues { (_, it) ->
        if (type.isCorrectionOrArt94or95()) {
            PaymentAccountAmountSummaryLine(
                priorityAxis = it.priorityAxis,
                totalEligibleExpenditure = it.correctedTotalEligibleWithoutArt94Or95.plus(it.unionContribution),
                totalPublicContribution = it.correctedFundAmount.plus(it.ofWhichPublic).plus(it.ofWhichAutoPublic)
            )
        } else {
            PaymentAccountAmountSummaryLine(
                priorityAxis = it.priorityAxis,
                totalEligibleExpenditure = it.fundAmount.plus(it.partnerContribution),
                totalPublicContribution = it.fundAmount.plus(it.ofWhichPublic).plus(it.ofWhichAutoPublic)
            )
        }
    }

fun Map<PaymentToEcOverviewType, Map<Long?, PaymentAccountAmountSummaryLine>>.mergeBothScoBases(): Map<Long?, PaymentAccountAmountSummaryLine> {
    val priorityIds = values.map { it.keys }.fold(emptySet<Long?>()) { a, b -> a union b }

    return priorityIds.associateWith { priorityId ->
        PaymentAccountAmountSummaryLine(
            priorityAxis = getAxisFor(priorityId),
            totalEligibleExpenditure = getTotalFor(priorityId),
            totalPublicContribution = getPublicFor(priorityId),
        )
    }
}

private fun Map<PaymentToEcOverviewType, Map<Long?, PaymentAccountAmountSummaryLine>>.getAxisFor(priorityId: Long?): String? =
    this.values.firstNotNullOfOrNull { it.getOrDefault(priorityId, null)?.priorityAxis }
private fun Map<PaymentToEcOverviewType, Map<Long?, PaymentAccountAmountSummaryLine>>.getTotalFor(priorityId: Long?): BigDecimal =
    this.onlyPriority(priorityId).sumOfNullable { it?.totalEligibleExpenditure }
private fun Map<PaymentToEcOverviewType, Map<Long?, PaymentAccountAmountSummaryLine>>.getPublicFor(priorityId: Long?): BigDecimal =
    this.onlyPriority(priorityId).sumOfNullable { it?.totalPublicContribution }

private fun Map<PaymentToEcOverviewType, Map<Long?, PaymentAccountAmountSummaryLine>>.onlyPriority(priorityId: Long?) =
    values.map { perPriority -> perPriority[priorityId] }
private inline fun <T> Iterable<T>.sumOfNullable(selector: (T) -> BigDecimal?): BigDecimal =
    sumOf { selector.invoke(it) ?: BigDecimal.ZERO }
