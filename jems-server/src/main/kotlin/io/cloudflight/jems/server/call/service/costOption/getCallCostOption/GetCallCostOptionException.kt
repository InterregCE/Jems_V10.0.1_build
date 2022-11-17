package io.cloudflight.jems.server.call.service.costOption.getCallCostOption

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CALL_COST_OPTION_ERROR_CODE_PREFIX = "S-UPC-GCO"
private const val GET_CALL_COST_OPTION_ERROR_KEY_PREFIX = "use.case.get.call.cost.option"

class GetCallCostOptionException(cause: Throwable) : ApplicationException(
    code = GET_CALL_COST_OPTION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CALL_COST_OPTION_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
