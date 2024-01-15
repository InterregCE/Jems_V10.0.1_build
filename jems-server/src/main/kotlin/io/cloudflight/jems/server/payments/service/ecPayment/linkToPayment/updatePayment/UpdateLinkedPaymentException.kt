package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_LINKED_PAYMENT_PREFIX = "S-ULPEC"
private const val UPDATE_LINKED_PAYMENT_ERROR_KEY_PREFIX = "use.case.update.linked.payment.ec"

class UpdateLinkedPaymentException(cause: Throwable) : ApplicationException(
    code = UPDATE_LINKED_PAYMENT_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_LINKED_PAYMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PaymentApplicationToEcNotInDraftException : ApplicationUnprocessableException(
    code = "$UPDATE_LINKED_PAYMENT_PREFIX-01",
    i18nMessage = I18nMessage("$UPDATE_LINKED_PAYMENT_ERROR_KEY_PREFIX.not.in.draft")
)
