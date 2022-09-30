package io.cloudflight.jems.server.payments.service.getPaymentDetail

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENT_DETAIL_ERROR_CODE_PREFIX = "S-GPD"
private const val GET_PAYMENT_DETAIL_ERROR_KEY_PREFIX = "use.case.get.payment.detail"

class GetPaymentDetailException(cause: Throwable) : ApplicationException(
    code = GET_PAYMENT_DETAIL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENT_DETAIL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
