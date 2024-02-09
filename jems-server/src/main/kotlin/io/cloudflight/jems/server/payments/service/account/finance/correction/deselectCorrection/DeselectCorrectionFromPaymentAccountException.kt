package io.cloudflight.jems.server.payments.service.account.finance.correction.deselectCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DESELECT_PAYMENT_FROM_PAYMENT_ACCOUNT_PREFIX = "S-DPFPA"
private const val DESELECT_PAYMENT_FROM_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX = "use.case.deselect.payment.from.payment.account"

class DeselectCorrectionFromPaymentAccountException(cause: Throwable) : ApplicationException(
    code = DESELECT_PAYMENT_FROM_PAYMENT_ACCOUNT_PREFIX,
    i18nMessage = I18nMessage("$DESELECT_PAYMENT_FROM_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PaymentAccountNotInDraftException : ApplicationUnprocessableException(
    code = "$DESELECT_PAYMENT_FROM_PAYMENT_ACCOUNT_PREFIX-001",
    i18nMessage = I18nMessage("$DESELECT_PAYMENT_FROM_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.not.in.draft"),
)
