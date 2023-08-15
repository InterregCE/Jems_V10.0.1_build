package io.cloudflight.jems.server.accountingYears.repository

import io.cloudflight.jems.server.accountingYears.service.AccountingYearPersistence
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class AccountingYearPersistenceProvider(
    private val repository: AccountingYearRepository,
) : AccountingYearPersistence {

    @Transactional(readOnly = true)
    override fun findAll(): List<AccountingYear> =
        repository.findAllByOrderByYear().toModel()

}
