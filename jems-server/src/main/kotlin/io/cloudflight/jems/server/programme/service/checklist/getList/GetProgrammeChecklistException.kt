package io.cloudflight.jems.server.programme.service.checklist.getList

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val GET_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX = "S-PCH"
const val GET_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX = "use.case.get.programme.checklist"

class GetProgrammeChecklistException(cause: Throwable) : ApplicationException(
    code = "$GET_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX.failed"), cause = cause
)

class GetProgrammeChecklistDetailNotFoundException : ApplicationNotFoundException(
    code = "$GET_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$GET_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX.detail.not.found"),
)
