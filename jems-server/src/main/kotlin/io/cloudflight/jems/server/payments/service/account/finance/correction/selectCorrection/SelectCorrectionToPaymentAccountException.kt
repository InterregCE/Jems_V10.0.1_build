package io.cloudflight.jems.server.payments.service.account.finance.correction.selectCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val SELECT_CORRECTION_TO_PAYMENT_ACCOUNT_PREFIX = "S-SCTPA"
private const val SELECT_CORRECTION_TO_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX = "use.case.select.correction.to.payment.account"

class SelectCorrectionToPaymentAccountException(cause: Throwable) : ApplicationException(
    code = SELECT_CORRECTION_TO_PAYMENT_ACCOUNT_PREFIX,
    i18nMessage = I18nMessage("$SELECT_CORRECTION_TO_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class CorrectionNotAvailableForSelectionException : ApplicationUnprocessableException(
    code = "$SELECT_CORRECTION_TO_PAYMENT_ACCOUNT_PREFIX-001",
    i18nMessage = I18nMessage("$SELECT_CORRECTION_TO_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.cannot.be.selected")
)

class PaymentAccountNotInDraftException : ApplicationUnprocessableException(
    code = "$SELECT_CORRECTION_TO_PAYMENT_ACCOUNT_PREFIX-002",
    i18nMessage = I18nMessage("$SELECT_CORRECTION_TO_PAYMENT_ACCOUNT_PREFIX.not.in.draft"),
)
