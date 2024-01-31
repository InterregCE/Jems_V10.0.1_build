package io.cloudflight.jems.server.payments.service.account.reOpenPaymentAccount

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val RE_OPEN_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX = "S-UPA"
private const val RE_OPEN_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX = "use.case.re.open.payment.account"

class ReOpenPaymentAccountException(cause: Throwable) : ApplicationException (
    code = RE_OPEN_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$RE_OPEN_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PaymentAccountNotSubmittedException: ApplicationUnprocessableException(
    code = "$RE_OPEN_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$RE_OPEN_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.not.in.submitted.status"),
)
