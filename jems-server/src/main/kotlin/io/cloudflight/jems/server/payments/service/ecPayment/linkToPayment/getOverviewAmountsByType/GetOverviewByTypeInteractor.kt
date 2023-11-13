package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsForArtNot94Not95

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType

interface GetOverviewByTypeInteractor {

    fun getOverviewAmountsByType(paymentToEcId: Long, type: PaymentToEcOverviewType?): PaymentToEcAmountSummary

}
