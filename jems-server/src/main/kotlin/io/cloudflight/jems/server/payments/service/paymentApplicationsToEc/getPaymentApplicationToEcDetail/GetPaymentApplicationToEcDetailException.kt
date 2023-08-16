package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.getPaymentApplicationToEcDetail

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX = "S-GPATED"
private const val DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX = "use.case.get.payment.applications.to.ec.detail"

class GetPaymentApplicationToEcDetailException(cause: Throwable) : ApplicationException(
    code = DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
