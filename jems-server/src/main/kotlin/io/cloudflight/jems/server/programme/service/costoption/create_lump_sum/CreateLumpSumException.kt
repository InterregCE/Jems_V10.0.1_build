package io.cloudflight.jems.server.programme.service.costoption.create_lump_sum

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_LUMP_SUM_ERROR_CODE_PREFIX = "S-LMP-COI"
private const val CREATE_LUMP_SUM_ERROR_KEY_PREFIX = "use.case.create.lump.sum"

class CreateLumpSumException(cause: Throwable) : ApplicationException(
    code = CREATE_LUMP_SUM_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_LUMP_SUM_ERROR_KEY_PREFIX.failed"), cause = cause
)

class IdHasToBeNull : ApplicationUnprocessableException(
    code = "$CREATE_LUMP_SUM_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_LUMP_SUM_ERROR_KEY_PREFIX.id.not.allowed"),
)

class MaxAllowedLumpSumsReached : ApplicationUnprocessableException(
    code = "$CREATE_LUMP_SUM_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_LUMP_SUM_ERROR_KEY_PREFIX.max.allowed.reached"),
)

class LumpSumIsInvalid(errors: Map<String, I18nMessage>) : ApplicationUnprocessableException(
    code = "$CREATE_LUMP_SUM_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CREATE_LUMP_SUM_ERROR_KEY_PREFIX.invalid"),
    formErrors = errors,
)
