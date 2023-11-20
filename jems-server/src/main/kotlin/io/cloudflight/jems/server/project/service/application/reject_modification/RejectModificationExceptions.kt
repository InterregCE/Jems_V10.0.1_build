package io.cloudflight.jems.server.project.service.application.reject_modification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val REJECT_MODIFICATION_ERROR_CODE_PREFIX = "S-PA-RA"
private const val REJECT_MODIFICATION_ERROR_KEY_PREFIX = "use.case.reject.modification"

class RejectModificationException(cause: Throwable) : ApplicationException(
    code = REJECT_MODIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$REJECT_MODIFICATION_ERROR_KEY_PREFIX.failed"), cause = cause
)

class CorrectionsNotValidException(invalid: Set<Long>): ApplicationUnprocessableException(
    code ="$REJECT_MODIFICATION_ERROR_CODE_PREFIX-01",
    i18nMessage =  I18nMessage("$REJECT_MODIFICATION_ERROR_CODE_PREFIX.corrections.invalid"),
    message = invalid.joinToString(", "),
)
