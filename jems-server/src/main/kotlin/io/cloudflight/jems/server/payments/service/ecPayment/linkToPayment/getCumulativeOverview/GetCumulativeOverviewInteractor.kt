package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeOverview

import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary

interface GetCumulativeOverviewInteractor {

    fun getCumulativeOverview(ecPaymentId: Long): PaymentToEcAmountSummary
}