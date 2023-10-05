package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import java.math.BigDecimal

fun constructFilter(
    ecApplicationId: Long,
    fundId: Long,
    scoBasis: PaymentSearchRequestScoBasis?,
    paymentType: PaymentType?,
) = PaymentSearchRequest(
    paymentId = null,
    paymentType = paymentType,
    projectIdentifiers = emptySet(),
    projectAcronym = null,
    claimSubmissionDateFrom = null,
    claimSubmissionDateTo = null,
    approvalDateFrom = null,
    approvalDateTo = null,
    fundIds = setOf(fundId),
    lastPaymentDateFrom = null,
    lastPaymentDateTo = null,
    availableForEcId = ecApplicationId,
    scoBasis = scoBasis,
)

fun Map<PaymentSearchRequestScoBasis, List<PaymentToEcAmountSummaryLineTmp>>.sumUpProperColumns() =
    mapValues { (_, totals) ->
        totals.map {
            PaymentToEcAmountSummaryLine(
                priorityAxis = it.priorityAxis,
                totalEligibleExpenditure = it.fundAmount.plus(it.partnerContribution),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = it.fundAmount.plus(it.ofWhichPublic).plus(it.ofWhichAutoPublic),
            )
        }
    }
