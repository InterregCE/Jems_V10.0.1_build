package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForModification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_AVAILABLE_CORRECTIONS_ERROR_CODE_PREFIX = "S-GACM"
private const val GET_AVAILABLE_CORRECTIONS_KEY_PREFIX = "use.case.get.project.audit.available.corrections.modification"

class GetAvailableCorrectionsForModificationException(cause: Throwable): ApplicationException(
    code = GET_AVAILABLE_CORRECTIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_AVAILABLE_CORRECTIONS_KEY_PREFIX.failed"),
    cause = cause
)
