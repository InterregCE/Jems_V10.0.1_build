package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.deselectCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DESELECT_CORRECTION_FROM_EC_PREFIX = "S-DCFEC"
private const val DESELECT_CORRECTION_FROM_EC_ERROR_KEY_PREFIX = "use.case.deselect.correction.from.ec"

class DeselectCorrectionFromEcException(cause: Throwable) : ApplicationException(
    code = DESELECT_CORRECTION_FROM_EC_PREFIX,
    i18nMessage = I18nMessage("$DESELECT_CORRECTION_FROM_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
