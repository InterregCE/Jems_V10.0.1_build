package io.cloudflight.jems.server.accountingYears.service.getAccountingYear

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_ACCOUNTING_YEARS_ERROR_CODE_PREFIX = "S-GAY"
private const val GET_ACCOUNTING_YEARS_ERROR_KEY_PREFIX = "use.case.get.accounting.years"

class GetAccountingYearsException(cause: Throwable) : ApplicationException (
    code = GET_ACCOUNTING_YEARS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_ACCOUNTING_YEARS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
