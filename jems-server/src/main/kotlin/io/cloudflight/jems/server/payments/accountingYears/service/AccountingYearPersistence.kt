package io.cloudflight.jems.server.payments.accountingYears.service

import io.cloudflight.jems.server.payments.model.ec.AccountingYear

interface AccountingYearPersistence {
    fun findAll(): List<AccountingYear>

    fun getById(id: Long): AccountingYear
}
