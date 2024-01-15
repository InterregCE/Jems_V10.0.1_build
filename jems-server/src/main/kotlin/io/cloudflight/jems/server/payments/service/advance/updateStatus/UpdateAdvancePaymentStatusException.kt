package io.cloudflight.jems.server.payments.service.advance.updateStatus

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_ADVANCE_PAYMENT_STATUS_ERROR_CODE_PREFIX = "S-UAPS"
private const val UPDATE_ADVANCE_PAYMENT_STATUS_ERROR_KEY_PREFIX = "use.case.update.advance.payment.status"

class UpdateAdvancePaymentStatusException(cause: Throwable) : ApplicationException(
    code = UPDATE_ADVANCE_PAYMENT_STATUS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_ADVANCE_PAYMENT_STATUS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
