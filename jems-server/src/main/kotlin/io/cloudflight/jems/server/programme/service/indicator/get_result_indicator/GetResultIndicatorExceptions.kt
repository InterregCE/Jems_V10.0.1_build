package io.cloudflight.jems.server.programme.service.indicator.get_result_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_RESULT_INDICATOR_ERROR_CODE_PREFIX = "S-IND-GRI"
const val GET_RESULT_INDICATOR_ERROR_KEY_PREFIX = "use.case.get.result.indicator"

class GetResultIndicatorException(cause: Throwable) : ApplicationException(
    code = GET_RESULT_INDICATOR_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_RESULT_INDICATOR_ERROR_KEY_PREFIX.failed"), cause = cause
)

