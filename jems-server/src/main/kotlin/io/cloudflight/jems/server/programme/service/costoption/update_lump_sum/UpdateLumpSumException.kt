package io.cloudflight.jems.server.programme.service.costoption.update_lump_sum

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_LUMP_SUM_ERROR_CODE_PREFIX = "S-LMP-UOI"
private const val UPDATE_LUMP_SUM_ERROR_KEY_PREFIX = "use.case.update.lump.sum"

class UpdateLumpSumException(cause: Throwable) : ApplicationException(
    code = UPDATE_LUMP_SUM_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_LUMP_SUM_ERROR_KEY_PREFIX.failed"), cause = cause
)

class LumpSumIsInvalid(errors: Map<String, I18nMessage>) : ApplicationUnprocessableException(
    code = "$UPDATE_LUMP_SUM_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_LUMP_SUM_ERROR_KEY_PREFIX.invalid"),
    formErrors = errors,
)
