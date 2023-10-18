package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getPayments.artNot94Not95

import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType

fun constructFilter(
    ecApplicationId: Long,
    fundId: Long,
    scoBasis: PaymentSearchRequestScoBasis,
    paymentType: PaymentType,
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
