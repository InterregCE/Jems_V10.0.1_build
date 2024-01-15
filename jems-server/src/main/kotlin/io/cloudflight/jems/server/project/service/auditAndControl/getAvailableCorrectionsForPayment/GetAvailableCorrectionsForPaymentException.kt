package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForPayment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_AVAILABLE_CORRECTIONS_ERROR_CODE_PREFIX = "S-GACP"
private const val GET_AVAILABLE_CORRECTIONS_KEY_PREFIX = "use.case.get.project.audit.available.corrections.payment"

class GetAvailableCorrectionsForPaymentException(cause: Throwable): ApplicationException(
    code = GET_AVAILABLE_CORRECTIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_AVAILABLE_CORRECTIONS_KEY_PREFIX.failed"),
    cause = cause
)

