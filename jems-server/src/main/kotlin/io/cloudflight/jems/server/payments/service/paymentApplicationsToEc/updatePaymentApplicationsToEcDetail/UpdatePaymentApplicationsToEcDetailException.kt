package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.updatePaymentApplicationsToEcDetail

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX = "S-UPATED"
private const val UPDATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX = "use.case.update.payment.applications.to.ec.detail"
class UpdatePaymentApplicationsToEcDetailException(cause: Throwable) : ApplicationException (
    code = UPDATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
