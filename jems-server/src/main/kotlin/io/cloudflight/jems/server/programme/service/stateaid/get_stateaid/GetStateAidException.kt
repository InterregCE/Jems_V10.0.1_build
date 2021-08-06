package io.cloudflight.jems.server.programme.service.stateaid.get_stateaid

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_STATE_AIDS_ERROR_CODE_PREFIX = "S-GSA"
const val GET_STATE_AIDS_ERROR_KEY_PREFIX = "use.case.get.state.aid"

class GetStateAidException(cause: Throwable) : ApplicationException(
    code = GET_STATE_AIDS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_STATE_AIDS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
