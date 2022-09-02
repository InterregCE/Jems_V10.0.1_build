package io.cloudflight.jems.server.call.service.costOption.updateCallCostOption

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_CALL_COST_OPTION_ERROR_CODE_PREFIX = "S-UPC-UCO"
private const val UPDATE_CALL_COST_OPTION_ERROR_KEY_PREFIX = "use.case.update.call.cost.option"

class UpdateCallCostOptionException(cause: Throwable) : ApplicationException(
    code = UPDATE_CALL_COST_OPTION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CALL_COST_OPTION_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class CallNotEditableException : ApplicationUnprocessableException(
    code = "$UPDATE_CALL_COST_OPTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CALL_COST_OPTION_ERROR_KEY_PREFIX.not.editable")
)
