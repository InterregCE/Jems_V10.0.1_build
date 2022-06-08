package io.cloudflight.jems.server.programme.service.checklist.delete

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val DELETE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX = "S-PCHD"
const val DELETE_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX = "use.case.delete.programme.checklist"

class DeleteProgrammeChecklistException(cause: Throwable) : ApplicationException(
    code = "$DELETE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX.failed"), cause = cause
)
