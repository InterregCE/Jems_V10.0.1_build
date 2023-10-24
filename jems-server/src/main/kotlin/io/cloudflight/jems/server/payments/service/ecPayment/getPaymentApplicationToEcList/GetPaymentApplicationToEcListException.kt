package io.cloudflight.jems.server.payments.service.ecPayment.getPaymentApplicationToEcList

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX = "S-GPATE"
private const val GET_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX = "use.case.get.payment.applications.to.ec"
class GetPaymentApplicationToEcListException(cause: Throwable) : ApplicationException (
    code = GET_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
