package io.cloudflight.jems.server.project.service.checklist.update.control

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-PCHC"
const val UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.update.control.checklist.instance"

class UpdateControlChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = "$UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class UpdateControlChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)