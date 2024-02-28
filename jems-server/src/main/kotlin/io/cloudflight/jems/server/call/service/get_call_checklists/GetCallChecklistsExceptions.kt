package io.cloudflight.jems.server.call.service.get_call_checklists

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CALL_CHECKLISTS_ERROR_CODE_PREFIX = "S-GPCL"
private const val GET_CALL_CHECKLISTS_ERROR_KEY_PREFIX = "use.case.get.call.checklists"

class GetCallChecklistsException(cause: Throwable) : ApplicationException(
    code = GET_CALL_CHECKLISTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CALL_CHECKLISTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
