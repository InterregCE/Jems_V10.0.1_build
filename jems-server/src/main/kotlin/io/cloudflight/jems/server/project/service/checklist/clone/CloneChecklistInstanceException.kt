package io.cloudflight.jems.server.project.service.checklist.clone

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val CLONE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CCIL"
const val CLONE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.clone.checklist.instance"

class CloneChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = "$CLONE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CLONE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)
