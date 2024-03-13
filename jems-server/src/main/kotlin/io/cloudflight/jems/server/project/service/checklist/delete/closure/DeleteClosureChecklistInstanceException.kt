package io.cloudflight.jems.server.project.service.checklist.delete.closure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val DELETE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-C-CHID"
const val DELETE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.delete.closure.checklist.instance"

class DeleteClosureChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = DELETE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class DeleteClosureChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$DELETE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)
