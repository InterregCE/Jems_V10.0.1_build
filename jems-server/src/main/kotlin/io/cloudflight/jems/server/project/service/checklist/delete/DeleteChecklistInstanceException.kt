package io.cloudflight.jems.server.programme.service.checklist.delete

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val DELETE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CHID"
const val DELETE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.delete.checklist.instance"

class DeleteChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = "$DELETE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)
