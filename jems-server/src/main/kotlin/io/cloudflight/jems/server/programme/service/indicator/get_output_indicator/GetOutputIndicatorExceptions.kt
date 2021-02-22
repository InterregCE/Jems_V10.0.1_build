package io.cloudflight.jems.server.programme.service.indicator.get_output_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_OUTPUT_INDICATOR_ERROR_CODE_PREFIX = "S-IND-GOI"
const val GET_OUTPUT_INDICATOR_ERROR_KEY_PREFIX = "use.case.get.output.indicator"

class GetOutputIndicatorException(cause: Throwable) : ApplicationException(
    code = GET_OUTPUT_INDICATOR_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.failed"), cause = cause
)

