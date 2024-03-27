package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.updateReconciliation

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-UR"
private const val ERROR_KEY_PREFIX = "use.case.update.reconciliation"

class UpdatePaymentReconciliationException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class PaymentAccountNotInDraftException: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-01",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.account.not.in.draft.exception")
)

