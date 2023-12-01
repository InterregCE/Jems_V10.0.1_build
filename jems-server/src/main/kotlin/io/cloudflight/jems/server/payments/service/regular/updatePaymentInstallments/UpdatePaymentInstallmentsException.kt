package io.cloudflight.jems.server.payments.service.regular.updatePaymentInstallments

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PAYMENT_INSTALLMENTS_ERROR_CODE_PREFIX = "S-UPPI"
private const val UPDATE_PAYMENT_INSTALLMENTS_ERROR_KEY_PREFIX = "use.case.update.payment.partner.installments"

class UpdatePaymentInstallmentsException(cause: Throwable) : ApplicationException(
    code = UPDATE_PAYMENT_INSTALLMENTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PAYMENT_INSTALLMENTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PaymentPartnerNotValidException: ApplicationUnprocessableException(
    code ="$UPDATE_PAYMENT_INSTALLMENTS_ERROR_CODE_PREFIX-01",
    i18nMessage =  I18nMessage("$UPDATE_PAYMENT_INSTALLMENTS_ERROR_KEY_PREFIX.failed"),
)

class CorrectionsNotValidException(invalidByPartnerId: Map<Long, Set<Long>>): ApplicationUnprocessableException(
    code ="$UPDATE_PAYMENT_INSTALLMENTS_ERROR_CODE_PREFIX-02",
    i18nMessage =  I18nMessage("$UPDATE_PAYMENT_INSTALLMENTS_ERROR_KEY_PREFIX.corrections.failed"),
    message = invalidByPartnerId.map { "Partner id=${it.key}: ${it.value}" }.joinToString(", "),
)
