package io.cloudflight.jems.server.payments.service.ecPayment

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import java.math.BigDecimal

fun constructFilter(
    ecPaymentIds: Set<Long?>,
    fundId: Long? = null,
    scoBasis: PaymentSearchRequestScoBasis?,
    paymentType: PaymentType? = null,
) = PaymentSearchRequest(
    paymentId = null,
    paymentType = paymentType,
    projectIdentifiers = emptySet(),
    projectAcronym = null,
    claimSubmissionDateFrom = null,
    claimSubmissionDateTo = null,
    approvalDateFrom = null,
    approvalDateTo = null,
    fundIds = if (fundId != null) setOf(fundId) else emptySet(),
    lastPaymentDateFrom = null,
    lastPaymentDateTo = null,
    ecPaymentIds = ecPaymentIds,
    scoBasis = scoBasis,
)

fun Map<PaymentSearchRequestScoBasis, Map<Long?, PaymentToEcAmountSummaryLineTmp>>.sumUpProperColumns() =
    mapValues { (_, totals) ->
        totals.mapValues { (_, it) ->
            PaymentToEcAmountSummaryLine(
                priorityAxis = it.priorityAxis,
                totalEligibleExpenditure = it.fundAmount.plus(it.partnerContribution),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = it.fundAmount.plus(it.ofWhichPublic).plus(it.ofWhichAutoPublic),
            )
        }
    }

fun Collection<PaymentToEcAmountSummaryLine>.sumUp() = PaymentToEcAmountSummaryLine (
    priorityAxis = if (allAxesSame()) firstOrNull()?.priorityAxis else null,
    totalEligibleExpenditure = sumOf { it.totalEligibleExpenditure },
    totalUnionContribution = sumOf { it.totalUnionContribution },
    totalPublicContribution = sumOf { it.totalPublicContribution }
)

fun Map<Long?, PaymentToEcAmountSummaryLine>.plus(other: Map<Long?, PaymentToEcAmountSummaryLine>): Map<Long?, PaymentToEcAmountSummaryLine> {
    val priorityIds = keys union other.keys

    return priorityIds.associateWith { priorityId ->
        PaymentToEcAmountSummaryLine(
            priorityAxis = get(priorityId)?.priorityAxis ?: other[priorityId]?.priorityAxis,
            totalEligibleExpenditure = get(priorityId)?.totalEligibleExpenditure.plusNullable(other[priorityId]?.totalEligibleExpenditure),
            totalUnionContribution = get(priorityId)?.totalUnionContribution.plusNullable(other[priorityId]?.totalUnionContribution),
            totalPublicContribution = get(priorityId)?.totalPublicContribution.plusNullable(other[priorityId]?.totalPublicContribution),
        )
    }
}

fun BigDecimal?.plusNullable(other: BigDecimal?): BigDecimal =
    (this ?: BigDecimal.ZERO).plus(other ?: BigDecimal.ZERO)

fun Map<PaymentSearchRequestScoBasis, Map<Long?, PaymentToEcAmountSummaryLine>>.mergeBothScoBases(): Map<Long?, PaymentToEcAmountSummaryLine> {
    val priorityIds = values.map { it.keys }.fold(emptySet<Long?>()) { a, b -> a union b }

    return priorityIds.associateWith { priorityId ->
        PaymentToEcAmountSummaryLine(
            priorityAxis = getAxisFor(priorityId),
            totalEligibleExpenditure = getTotalFor(priorityId),
            totalUnionContribution = getUnionFor(priorityId),
            totalPublicContribution = getPublicFor(priorityId),
        )
    }
}

private fun Map<PaymentSearchRequestScoBasis, Map<Long?, PaymentToEcAmountSummaryLine>>.getAxisFor(priorityId: Long?): String? =
    this.values.firstNotNullOfOrNull { it.getOrDefault(priorityId, null)?.priorityAxis }
private fun Map<PaymentSearchRequestScoBasis, Map<Long?, PaymentToEcAmountSummaryLine>>.getTotalFor(priorityId: Long?): BigDecimal =
    this.values.map { it.getOrDefault(priorityId, null) }.sumOf { it?.totalEligibleExpenditure ?: BigDecimal.ZERO }
private fun Map<PaymentSearchRequestScoBasis, Map<Long?, PaymentToEcAmountSummaryLine>>.getUnionFor(priorityId: Long?): BigDecimal =
    this.values.map { it.getOrDefault(priorityId, null) }.sumOf { it?.totalUnionContribution ?: BigDecimal.ZERO }
private fun Map<PaymentSearchRequestScoBasis, Map<Long?, PaymentToEcAmountSummaryLine>>.getPublicFor(priorityId: Long?): BigDecimal =
    this.values.map { it.getOrDefault(priorityId, null) }.sumOf { it?.totalPublicContribution ?: BigDecimal.ZERO }

 fun Collection<PaymentToEcAmountSummaryLine>.allAxesSame() = mapTo(HashSet()) { it.priorityAxis }.size == 1
