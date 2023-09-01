package io.cloudflight.jems.server.payments.accountingYears.service

import io.cloudflight.jems.server.payments.model.regular.AccountingYear

interface AccountingYearPersistence {
    fun findAll(): List<AccountingYear>
}
