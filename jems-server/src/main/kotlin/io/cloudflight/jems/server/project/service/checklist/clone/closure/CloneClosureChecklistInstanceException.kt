package io.cloudflight.jems.server.project.service.checklist.clone.closure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val CLONE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CR-CCIL"
const val CLONE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.clone.closure.checklist.instance"

class CloneClosureChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = "$CLONE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CLONE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class CloneClosureChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$CLONE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CLONE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)
