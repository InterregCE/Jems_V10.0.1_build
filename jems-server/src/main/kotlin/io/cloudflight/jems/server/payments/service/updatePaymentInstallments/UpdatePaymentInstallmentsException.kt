package io.cloudflight.jems.server.payments.service.updatePaymentInstallments

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PAYMENT_INSTALLMENTS_ERROR_CODE_PREFIX = "S-UPPI"
private const val UPDATE_PAYMENT_INSTALLMENTS_ERROR_KEY_PREFIX = "use.case.update.payment.partner.installments"

class UpdatePaymentInstallmentsException(cause: Throwable) : ApplicationException(
    code = UPDATE_PAYMENT_INSTALLMENTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PAYMENT_INSTALLMENTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
