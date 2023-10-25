package io.cloudflight.jems.server.payments.service.ecPayment.getAvailableAccountingYearsForPaymentFund

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_AVAILABLE_ACCOUNTING_YEARS_FOR_FUND_ERROR_CODE_PREFIX = "S-GAACCYPF"
private const val GET_AVAILABLE_ACCOUNTING_YEARS_FOR_FUND_ERROR_KEY_PREFIX = "use.case.get.available.acc.years.for.ec.payment.fund"

class GetAvailableAccountingYearsForPaymentFundException(cause: Throwable): ApplicationException (
    code = GET_AVAILABLE_ACCOUNTING_YEARS_FOR_FUND_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_AVAILABLE_ACCOUNTING_YEARS_FOR_FUND_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
