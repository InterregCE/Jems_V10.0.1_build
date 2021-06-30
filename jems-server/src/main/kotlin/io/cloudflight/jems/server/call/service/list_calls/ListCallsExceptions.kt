package io.cloudflight.jems.server.call.service.list_calls

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_CALLS_ERROR_CODE_PREFIX = "S-LC"
private const val LIST_CALLS_ERROR_KEY_PREFIX = "use.case.list.calls"

class ListCallsException(cause: Throwable) : ApplicationException(
    code = LIST_CALLS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_CALLS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
