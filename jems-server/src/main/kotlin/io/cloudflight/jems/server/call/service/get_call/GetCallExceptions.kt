package io.cloudflight.jems.server.call.service.get_call

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CALL_ERROR_CODE_PREFIX = "S-GPC"
private const val GET_CALL_ERROR_KEY_PREFIX = "use.case.get.call"

class GetCallException(cause: Throwable) : ApplicationException(
    code = GET_CALL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CALL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
