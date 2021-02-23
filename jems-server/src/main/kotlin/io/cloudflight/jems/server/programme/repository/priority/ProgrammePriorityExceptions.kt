package io.cloudflight.jems.server.programme.repository.priority

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val PROGRAMME_PRIORITY_ERROR_CODE_PREFIX = "R-PRS-PP"

class ProgrammeSpecificObjectiveNotFoundException : ApplicationNotFoundException(
    code = "$PROGRAMME_PRIORITY_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("programme.specific.objective.not.found"), cause = null
)

