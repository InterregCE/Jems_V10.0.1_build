package io.cloudflight.jems.server.call.service.update_call_checklists

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_CALL_CHECKLISTS_ERROR_CODE_PREFIX = "S-UPCL"
private const val UPDATE_CALL_CHECKLISTS_ERROR_KEY_PREFIX = "use.case.update.call.checklists"

class UpdateCallChecklistsException(cause: Throwable) : ApplicationException(
    code = UPDATE_CALL_CHECKLISTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CALL_CHECKLISTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
