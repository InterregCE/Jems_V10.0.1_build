package io.cloudflight.jems.server.accountingYears.controller

import io.cloudflight.jems.api.accountingYear.AccountingYearApi
import io.cloudflight.jems.api.accountingYear.AccountingYearDTO
import io.cloudflight.jems.server.accountingYears.service.getAccountingYear.GetAccountingYearInteractor
import io.cloudflight.jems.server.accountingYears.service.toDto
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountingYearController(
    private val getAccountingYears: GetAccountingYearInteractor
) : AccountingYearApi {

    override fun getAccountingYears(): List<AccountingYearDTO> =
        getAccountingYears.getAccountingYears().toDto()
}
