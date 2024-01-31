package io.cloudflight.jems.server.payments.service.account.finalizePaymentAccount

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val FINISH_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX = "S-UPA"
private const val FINISH_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX = "use.case.finish.payment.account"

class FinalizePaymentAccountException(cause: Throwable) : ApplicationException(
    code = FINISH_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$FINISH_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class EcPaymentsForAccountingYearStillInDraftException(ecPayments: Map<String, String>): ApplicationUnprocessableException(
    code = "$FINISH_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$FINISH_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.ec.payments.for.accounting.years.not.in.draft", i18nArguments = ecPayments),
)

class PaymentAccountNotInDraftException: ApplicationUnprocessableException(
    code = "$FINISH_PAYMENT_ACCOUNT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(i18nKey = "$FINISH_PAYMENT_ACCOUNT_ERROR_KEY_PREFIX.not.in.draft"),
)
