package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsForArtNot94Not95

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis

interface GetCumulativeAmountsByTypeInteractor {

    fun getCumulativeAmountsByType(paymentToEcId: Long, type: PaymentSearchRequestScoBasis?): PaymentToEcAmountSummary

}
