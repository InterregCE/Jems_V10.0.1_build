package io.cloudflight.jems.server.payments.service.ecPayment.getAvailableAccountingYearsForPaymentFund

import io.cloudflight.jems.server.payments.model.ec.AccountingYearAvailability

interface GetAvailableAccountingYearsForPaymentFundInteractor {

    fun getAvailableAccountingYearsForPaymentFund(programmeFundId: Long): List<AccountingYearAvailability>

}
