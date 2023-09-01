package io.cloudflight.jems.server.payments.repository.regular

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val PAYMENT_REGULAR_PERSISTENCE_ERROR_CODE = "R-PRP"
class PaymentFinancingSourceNotFoundException : ApplicationNotFoundException(
    code = "$PAYMENT_REGULAR_PERSISTENCE_ERROR_CODE-001",
    i18nMessage = I18nMessage("regular.payment.fund.not.found"),
)