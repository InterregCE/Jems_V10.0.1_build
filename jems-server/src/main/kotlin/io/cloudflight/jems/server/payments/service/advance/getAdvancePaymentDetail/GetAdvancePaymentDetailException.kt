package io.cloudflight.jems.server.payments.service.advance.getAdvancePaymentDetail

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_ADV_PAYMENT_DETAIL_ERROR_CODE_PREFIX = "S-GAP"
private const val GET_ADV_PAYMENT_DETAIL_ERROR_KEY_PREFIX = "use.case.get.advance.payment.detail"

class GetAdvancePaymentDetailException(cause: Throwable) : ApplicationException(
    code = GET_ADV_PAYMENT_DETAIL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_ADV_PAYMENT_DETAIL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
