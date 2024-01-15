package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.selectCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val SELECT_CORRECTION_TO_EC_PREFIX = "S-SCTEC"
private const val SELECT_CORRECTION_TO_EC_ERROR_KEY_PREFIX = "use.case.select.correction.to.ec"

class SelectCorrectionToEcException(cause: Throwable) : ApplicationException(
    code = SELECT_CORRECTION_TO_EC_PREFIX,
    i18nMessage = I18nMessage("$SELECT_CORRECTION_TO_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class CorrectionNotAvailableForSelectionException : ApplicationUnprocessableException(
    code = "$SELECT_CORRECTION_TO_EC_PREFIX-001",
    i18nMessage = I18nMessage("$SELECT_CORRECTION_TO_EC_ERROR_KEY_PREFIX.cannot.be.selected")
)
