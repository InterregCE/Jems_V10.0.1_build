package io.cloudflight.jems.server.project.service.application.set_application_to_closed

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val SET_APPLICATION_TO_CLOSED_ERROR_CODE_PREFIX = "S-SACL"
private const val SET_APPLICATION_TO_CLOSED_ERROR_KEY_PREFIX = "use.case.set.application.to.closed"

class SetApplicationToClosedException(cause: Throwable) : ApplicationException(
    code = SET_APPLICATION_TO_CLOSED_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_APPLICATION_TO_CLOSED_ERROR_KEY_PREFIX.failed"), cause = cause
)
