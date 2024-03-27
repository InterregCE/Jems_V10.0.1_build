package io.cloudflight.jems.server.payments.service.account.finance.getAmountSummary

import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary

interface GetPaymentAccountAmountSummaryInteractor {

    fun getSummaryOverview(paymentAccountId: Long): PaymentAccountAmountSummary
}
