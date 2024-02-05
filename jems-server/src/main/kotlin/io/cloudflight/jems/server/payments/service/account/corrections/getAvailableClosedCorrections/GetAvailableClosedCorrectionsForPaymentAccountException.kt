package io.cloudflight.jems.server.payments.service.account.corrections.getAvailableClosedCorrections

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CORRECTIONS_PAYMENT_ACCOUNT_PREFIX = "S-GCPA"
private const val GET_CORRECTIONS_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX = "use.case.get.corrections.payment.account"

class GetAvailableClosedCorrectionsForPaymentAccountException(cause: Throwable) : ApplicationException(
    code = GET_CORRECTIONS_PAYMENT_ACCOUNT_PREFIX,
    i18nMessage = I18nMessage("$GET_CORRECTIONS_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
