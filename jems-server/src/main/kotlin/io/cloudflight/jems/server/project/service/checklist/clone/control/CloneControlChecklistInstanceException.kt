package io.cloudflight.jems.server.project.service.checklist.clone.control

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val CLONE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-C-CCIL"
const val CLONE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.clone.control.checklist.instance"

class CloneControlChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = "$CLONE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CLONE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class CloneControlChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$CLONE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CLONE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)
