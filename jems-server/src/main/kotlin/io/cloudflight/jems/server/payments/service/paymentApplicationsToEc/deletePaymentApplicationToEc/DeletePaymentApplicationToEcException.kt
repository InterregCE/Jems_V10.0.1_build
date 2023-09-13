package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.deletePaymentApplicationToEc

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX = "S-DPATE"
private const val DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX = "use.case.delete.payment.applications.to.ec"

class DeletePaymentApplicationToEcException(cause: Throwable) : ApplicationException (
    code = DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)


class PaymentFinishedException : ApplicationUnprocessableException(
    code = "$DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX-01",
    i18nMessage = I18nMessage("$DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX.payment.finished"),
)
