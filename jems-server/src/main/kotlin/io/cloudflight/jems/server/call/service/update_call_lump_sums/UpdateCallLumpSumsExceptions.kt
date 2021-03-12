package io.cloudflight.jems.server.call.service.update_call_lump_sums

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val UPDATE_CALL_LUMP_SUMS_ERROR_CODE_PREFIX = "S-UPC-LS"
private const val UPDATE_CALL_LUMP_SUMS_ERROR_KEY_PREFIX = "use.case.update.call.lumpSums"

class UpdateCallLumpSumsException(cause: Throwable) : ApplicationException(
    code = UPDATE_CALL_LUMP_SUMS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CALL_LUMP_SUMS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class LumpSumNotFound : ApplicationNotFoundException(
    code = "$UPDATE_CALL_LUMP_SUMS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CALL_LUMP_SUMS_ERROR_KEY_PREFIX.lumpSum.not.found"),
)

class LumpSumsRemovedAfterCallPublished : ApplicationBadRequestException(
    code = "$UPDATE_CALL_LUMP_SUMS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CALL_LUMP_SUMS_ERROR_KEY_PREFIX.removing.or.updating.existing.lumpSums.is.forbidden"),
)
