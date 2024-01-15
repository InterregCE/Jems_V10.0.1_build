package io.cloudflight.jems.server.project.service.application.approve_modification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val APPROVE_MODIFICATION_ERROR_CODE_PREFIX = "S-PA-AM"
private const val APPROVE_MODIFICATION_ERROR_KEY_PREFIX = "use.case.approve.modification"

class ApproveModificationException(cause: Throwable) : ApplicationException(
    code = APPROVE_MODIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$APPROVE_MODIFICATION_ERROR_KEY_PREFIX.failed"), cause = cause
)

class CorrectionsNotValidException(invalid: Set<Long>): ApplicationUnprocessableException(
    code ="$APPROVE_MODIFICATION_ERROR_CODE_PREFIX-001",
    i18nMessage =  I18nMessage("$APPROVE_MODIFICATION_ERROR_KEY_PREFIX.corrections.invalid"),
    message = invalid.joinToString(", "),
)
