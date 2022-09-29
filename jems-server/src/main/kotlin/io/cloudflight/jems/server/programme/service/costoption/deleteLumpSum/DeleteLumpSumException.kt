package io.cloudflight.jems.server.programme.service.costoption.deleteLumpSum

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException

const val DELETE_LUMP_SUM_ERROR_CODE_PREFIX = "S-LMP-DLS"
const val DELETE_LUMP_SUM_ERROR_KEY_PREFIX = "use.case.delete.lump.sum"

class DeleteLumpSumFailed(cause: Throwable) : ApplicationException(
    code = DELETE_LUMP_SUM_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_LUMP_SUM_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class DeleteLumpSumWhenProgrammeSetupRestricted : ApplicationAccessDeniedException(
    code = "${DELETE_LUMP_SUM_ERROR_CODE_PREFIX}_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_LUMP_SUM_ERROR_KEY_PREFIX.programme.setup.restricted"),
)

class ToDeleteLumpSumAlreadyUsedInCall: ApplicationBadRequestException(
    code = "$DELETE_LUMP_SUM_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$DELETE_LUMP_SUM_ERROR_KEY_PREFIX.already.used.in.call"),
)
