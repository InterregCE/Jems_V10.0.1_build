package io.cloudflight.jems.server.programme.repository.stateaid

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val PROGRAMME_STATE_AID_ERROR_CODE_PREFIX = "R-PRS-SA"

class ProgrammeStateAidNotFoundException : ApplicationNotFoundException(
    code = "$PROGRAMME_STATE_AID_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("programme.state.aid.not.found"), cause = null
)

