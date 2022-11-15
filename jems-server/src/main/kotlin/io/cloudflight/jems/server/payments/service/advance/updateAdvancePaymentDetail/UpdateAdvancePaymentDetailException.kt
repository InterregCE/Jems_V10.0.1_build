package io.cloudflight.jems.server.payments.service.advance.updateAdvancePaymentDetail

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_ADV_PAYMENT_DETAIL_ERROR_CODE_PREFIX = "S-UAPI"
private const val UPDATE_ADV_PAYMENT_DETAIL_ERROR_KEY_PREFIX = "use.case.update.payment.advance.detail"

class UpdateAdvancePaymentDetailException(cause: Throwable) : ApplicationException(
    code = UPDATE_ADV_PAYMENT_DETAIL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_ADV_PAYMENT_DETAIL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
