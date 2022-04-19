package io.cloudflight.jems.server.programme.service.checklist.update

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX = "S-PCHC"
const val UPDATE_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX = "use.case.update.programme.checklist"

class UpdateProgrammeChecklistException(cause: Throwable) : ApplicationException(
    code = "$UPDATE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX.failed"), cause = cause
)

class ChecklistLockedException : ApplicationUnprocessableException(
    code = "$UPDATE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX.locked")
)
