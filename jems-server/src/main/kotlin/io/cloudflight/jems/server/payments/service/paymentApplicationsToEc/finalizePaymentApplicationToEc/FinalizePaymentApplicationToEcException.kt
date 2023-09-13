package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.finalizePaymentApplicationToEc

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val FINALIZE_PAYMENT_APPLICATION_TO_EC_ERROR_CODE_PREFIX = "S-FPATED"
private const val FINALIZE_PAYMENT_APPLICATION_TO_EC_ERROR_KEY_PREFIX =
    "use.case.finalize.payment.applications.to.ec.detail"

class FinalizePaymentApplicationToEcException(cause: Throwable) : ApplicationException(
    code = FINALIZE_PAYMENT_APPLICATION_TO_EC_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$FINALIZE_PAYMENT_APPLICATION_TO_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PaymentApplicationToEcNotInDraftException : ApplicationUnprocessableException(
    code = "$FINALIZE_PAYMENT_APPLICATION_TO_EC_ERROR_CODE_PREFIX-01",
    i18nMessage = I18nMessage("$FINALIZE_PAYMENT_APPLICATION_TO_EC_ERROR_KEY_PREFIX.not.in.draft")
)
