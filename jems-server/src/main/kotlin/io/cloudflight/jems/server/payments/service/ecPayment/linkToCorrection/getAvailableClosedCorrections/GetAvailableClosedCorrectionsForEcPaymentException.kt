package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.getAvailableClosedCorrections

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CORRECTIONS_NO_CLERICAL_MISTAKE_PREFIX = "S-GCNCM"
private const val GET_CORRECTIONS_NO_CLERICAL_MISTAKE_ERROR_KEY_PREFIX = "use.case.get.corrections.no.clerical.mistakes"

class GetAvailableClosedCorrectionsForEcPaymentException(cause: Throwable) : ApplicationException(
    code = GET_CORRECTIONS_NO_CLERICAL_MISTAKE_PREFIX,
    i18nMessage = I18nMessage("$GET_CORRECTIONS_NO_CLERICAL_MISTAKE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
