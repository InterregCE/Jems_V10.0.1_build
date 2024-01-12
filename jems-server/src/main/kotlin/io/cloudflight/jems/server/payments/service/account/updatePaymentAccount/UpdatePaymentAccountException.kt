package io.cloudflight.jems.server.payments.service.account.updatePaymentAccount

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX = "S-UPA"
private const val UPDATE_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX = "use.case.update.payment.account"

class UpdatePaymentAccountException(cause: Throwable) : ApplicationException (
    code = UPDATE_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
