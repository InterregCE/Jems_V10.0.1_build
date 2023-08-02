package io.cloudflight.jems.server.accountingYears.service.getAccountingYear

import io.cloudflight.jems.server.accountingYears.service.AccountingYearPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.model.regular.AccountingYear
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAccountingYears(
    private val persistence: AccountingYearPersistence
): GetAccountingYearInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAccountingYearsException::class)
    override fun getAccountingYears(): List<AccountingYear> =
        persistence.findAll()

}
