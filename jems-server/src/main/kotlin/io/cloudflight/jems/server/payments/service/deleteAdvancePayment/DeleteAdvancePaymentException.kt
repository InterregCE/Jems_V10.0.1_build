package io.cloudflight.jems.server.payments.service.deleteAdvancePayment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DELETE_ADV_PAYMENT_ERROR_CODE_PREFIX = "S-DAPI"
private const val DELETE_ADV_PAYMENT_ERROR_KEY_PREFIX = "use.case.delete.advance.payment"

class DeleteAdvancePaymentException(cause: Throwable) : ApplicationException(
    code = DELETE_ADV_PAYMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_ADV_PAYMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class DeleteAdvancePaymentNotFoundException : ApplicationNotFoundException(
    code = "$DELETE_ADV_PAYMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_ADV_PAYMENT_ERROR_KEY_PREFIX.not.found"),
)
