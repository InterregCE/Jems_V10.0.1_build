package io.cloudflight.jems.server.payments.service.account.listPaymentAccount

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX = "S-LPA"
private const val LIST_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX = "use.case.get.list.payment.account"

class ListPaymentAccountException(cause: Throwable) : ApplicationException (
    code = LIST_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
