package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.selectPayment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val SELECT_PAYMENT_TO_EC_PREFIX = "S-SPTEC"
private const val SELECT_PAYMENT_TO_EC_ERROR_KEY_PREFIX = "use.case.select.payment.to.ec"

class SelectPaymentToEcException(cause: Throwable) : ApplicationException(
    code = SELECT_PAYMENT_TO_EC_PREFIX,
    i18nMessage = I18nMessage("$SELECT_PAYMENT_TO_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PaymentApplicationToEcNotInDraftException : ApplicationUnprocessableException(
    code = "$SELECT_PAYMENT_TO_EC_PREFIX-001",
    i18nMessage = I18nMessage("$SELECT_PAYMENT_TO_EC_ERROR_KEY_PREFIX.not.in.draft"),
)

class PaymentApplicationAlreadyTakenException(ecPaymentId: Long) : ApplicationUnprocessableException(
    code = "$SELECT_PAYMENT_TO_EC_PREFIX-002",
    i18nMessage = I18nMessage(
        i18nKey = "$SELECT_PAYMENT_TO_EC_ERROR_KEY_PREFIX.already.taken",
        i18nArguments = mapOf("ecPaymentId" to ecPaymentId.toString()),
    ),
    message = "you need to deselect it first from previous EC Payment Application id=$ecPaymentId",
)
