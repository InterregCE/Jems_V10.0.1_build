package io.cloudflight.jems.api.payments.applicationToEc

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping

@Api("Accounting Year")
interface AccountingYearApi {

    companion object {
        const val ENDPOINT_API_EC_PAYMENTS = "/api/accountingYears"
    }

    @ApiOperation("Returns all accounting years")
    @GetMapping(ENDPOINT_API_EC_PAYMENTS)
    fun getAccountingYears(): List<AccountingYearDTO>
}
