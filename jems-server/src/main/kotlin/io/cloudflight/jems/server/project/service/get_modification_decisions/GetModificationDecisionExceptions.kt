package io.cloudflight.jems.server.project.service.get_modification_decisions

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_MODIFICATION_DECISIONS_ERROR_CODE_PREFIX = "S-GMD"
const val GET_MODIFICATION_DECISIONS_ERROR_KEY_PREFIX = "use.case.get.modification.decisions"

class GetModificationDecisionExceptions(cause: Throwable) : ApplicationException(
    code = GET_MODIFICATION_DECISIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_MODIFICATION_DECISIONS_ERROR_KEY_PREFIX.failed"), cause = cause
)
