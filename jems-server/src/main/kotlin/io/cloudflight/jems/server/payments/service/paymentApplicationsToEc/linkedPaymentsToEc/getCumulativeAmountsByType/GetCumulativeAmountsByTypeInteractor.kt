package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getCumulativeAmountsForArtNot94Not95

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis

interface GetCumulativeAmountsByTypeInteractor {

    fun getCumulativeAmountsByType(paymentToEcId: Long, type: PaymentSearchRequestScoBasis?): PaymentToEcAmountSummary

}
