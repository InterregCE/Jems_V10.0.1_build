package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getAvailableAccountingYearsForPaymentFund

import io.cloudflight.jems.server.payments.model.regular.AccountingYear

interface GetAvailableAccountingYearsForPaymentFundInteractor {

    fun getAvailableAccountingYearsForPaymentFund(programmeFundId: Long): List<AccountingYear>

}