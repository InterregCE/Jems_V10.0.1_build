package io.cloudflight.jems.server.call.service.update_allow_real_costs

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_CALL_ALLOWED_REAL_COSTS_ERROR_CODE_PREFIX = "S-UPC-ARC"
private const val UPDATE_CALL_ALLOWED_REAL_COSTS_ERROR_KEY_PREFIX = "use.case.update.call.allow.real.costs"

class UpdateAllowedRealCostsExceptions(cause: Throwable) : ApplicationException(
    code = UPDATE_CALL_ALLOWED_REAL_COSTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CALL_ALLOWED_REAL_COSTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class CallNotEditableException : ApplicationUnprocessableException(
    code = "$UPDATE_CALL_ALLOWED_REAL_COSTS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CALL_ALLOWED_REAL_COSTS_ERROR_KEY_PREFIX.not.editable")
)
