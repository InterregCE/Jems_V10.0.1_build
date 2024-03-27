package io.cloudflight.jems.server.payments.service.ecPayment.reOpenFinalizedEcPaymentApplication

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-RECP"
private const val ERROR_KEY_PREFIX = "use.case.reopen.ecPayment"

class ReOpenFinalizedEcPaymentApplicationException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class EcPaymentNotFinishedException : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-01",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.not.finished")
)

class ThereIsOtherEcPaymentInDraftException : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-02",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.there.is.other.ecPayment.open"),
)

class AccountingYearHasBeenAlreadyFinishedException : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-03",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.accountingYear.already.finished"),
)
