package io.cloudflight.jems.server.payments.service.account.finance.correction.updateCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_LINKED_CORRECTION_PAYMENT_ACCOUNT_PREFIX = "S-ULCPA"
private const val UPDATE_LINKED_CORRECTION_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX = "use.case.update.linked.correction.to.payment.account"

class UpdateLinkedCorrectionToPaymentAccountException(cause: Throwable) : ApplicationException(
    code = UPDATE_LINKED_CORRECTION_PAYMENT_ACCOUNT_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_LINKED_CORRECTION_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PaymentAccountNotInDraftException : ApplicationUnprocessableException(
    code = "$UPDATE_LINKED_CORRECTION_PAYMENT_ACCOUNT_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_LINKED_CORRECTION_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.not.in.draft"),
)
