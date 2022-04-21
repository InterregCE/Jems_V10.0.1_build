package io.cloudflight.jems.server.programme.service.checklist.create

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val CREATE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CHIC"
const val CREATE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.create.checklist.instance"

class CreateChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = "$CREATE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)
