package io.cloudflight.jems.server.payments.service.ecPayment.reOpenFinalizedEcPaymentApplication

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException


private const val REOPEN_PAYMENT_APPLICATION_TO_EC_ERROR_CODE_PREFIX = "S-RPATED"
private const val REOPEN__PAYMENT_APPLICATION_TO_EC_ERROR_KEY_PREFIX = "use.case.reopen.payment.applications.to.ec"

class ReOpenFinalizedEcPaymentApplicationException (cause: Throwable) : ApplicationException(
    code = REOPEN_PAYMENT_APPLICATION_TO_EC_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$REOPEN__PAYMENT_APPLICATION_TO_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class EcPaymentApplicationNotFinishedException : ApplicationUnprocessableException(
    code = "$REOPEN_PAYMENT_APPLICATION_TO_EC_ERROR_CODE_PREFIX-01",
    i18nMessage = I18nMessage("$REOPEN__PAYMENT_APPLICATION_TO_EC_ERROR_KEY_PREFIX.not.finished")
)
