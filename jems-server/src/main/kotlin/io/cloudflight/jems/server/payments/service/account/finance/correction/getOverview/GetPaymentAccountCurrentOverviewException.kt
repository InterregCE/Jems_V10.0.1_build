package io.cloudflight.jems.server.payments.service.account.finance.correction.getOverview

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENT_ACCOUNT_CURRENT_OVERVIEW_AMOUNTS_PREFIX = "S-GPACOAP"
private const val GET_PAYMENT_ACCOUNT_CURRENT_OVERVIEW_AMOUNTS_PREFIX_ERROR_KEY_PREFIX =
    "use.case.get.payment.account.current.overview.amounts"

class GetPaymentAccountCurrentOverviewException(cause: Throwable) : ApplicationException(
    code = GET_PAYMENT_ACCOUNT_CURRENT_OVERVIEW_AMOUNTS_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENT_ACCOUNT_CURRENT_OVERVIEW_AMOUNTS_PREFIX_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
