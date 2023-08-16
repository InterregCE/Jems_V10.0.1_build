package io.cloudflight.jems.server.payments.accountingYears.service.getAccountingYear

import io.cloudflight.jems.server.payments.model.regular.AccountingYear

interface GetAccountingYearInteractor {

    fun getAccountingYears(): List<AccountingYear>

}
