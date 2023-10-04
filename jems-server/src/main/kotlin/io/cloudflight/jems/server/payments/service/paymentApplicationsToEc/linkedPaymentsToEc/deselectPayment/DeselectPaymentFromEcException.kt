package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.deselectPayment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DESELECT_PAYMENT_FROM_EC_PREFIX = "S-DPFEC"
private const val DESELECT_PAYMENT_FROM_EC_ERROR_KEY_PREFIX = "use.case.deselect.payment.from.ec"

class DeselectPaymentFromEcException(cause: Throwable) : ApplicationException(
    code = DESELECT_PAYMENT_FROM_EC_PREFIX,
    i18nMessage = I18nMessage("$DESELECT_PAYMENT_FROM_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PaymentApplicationToEcNotInDraftException : ApplicationUnprocessableException(
    code = "$DESELECT_PAYMENT_FROM_EC_PREFIX-001",
    i18nMessage = I18nMessage("$DESELECT_PAYMENT_FROM_EC_ERROR_KEY_PREFIX.not.in.draft"),
)
