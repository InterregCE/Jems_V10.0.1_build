package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments

import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType

fun constructFilter(
    paymentType: PaymentType?,
    ecPaymentIds: Set<Long?>,
    fundId: Long? = null,
    contractingScoBasis: PaymentSearchRequestScoBasis? = null,
    finalScoBasis: PaymentSearchRequestScoBasis? = null,
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
    contractingScoBasis = contractingScoBasis,
    finalScoBasis = finalScoBasis,
)
