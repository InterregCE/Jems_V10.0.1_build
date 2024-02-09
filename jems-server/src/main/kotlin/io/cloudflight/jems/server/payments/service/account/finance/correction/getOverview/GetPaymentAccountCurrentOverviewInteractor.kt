package io.cloudflight.jems.server.payments.service.account.finance.correction.getOverview

import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummary

interface GetPaymentAccountCurrentOverviewInteractor {

    fun getCurrentOverview(paymentAccountId: Long): PaymentAccountAmountSummary
}
