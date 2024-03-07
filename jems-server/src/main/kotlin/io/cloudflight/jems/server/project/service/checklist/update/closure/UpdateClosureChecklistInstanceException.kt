package io.cloudflight.jems.server.project.service.checklist.update.closure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-C-PCHC"
const val UPDATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.update.closure.checklist.instance"

class UpdateClosureChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = UPDATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class UpdateClosureChecklistInstanceNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)

class UpdateClosureChecklistInstanceNotFoundException : ApplicationNotFoundException(
    code = "$UPDATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.not.found")
)
