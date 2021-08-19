package io.cloudflight.jems.server.call.service.get_allow_real_costs

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CALL_ALLOW_REAL_COSTS_ERROR_CODE_PREFIX = "S-UPC-ARC"
private const val GET_CALL_ALLOW_REAL_COSTS_ERROR_KEY_PREFIX = "use.case.get.call.allow.real.costs"

class GetAllowRealCostsExceptions(cause: Throwable) : ApplicationException(
    code = GET_CALL_ALLOW_REAL_COSTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CALL_ALLOW_REAL_COSTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
