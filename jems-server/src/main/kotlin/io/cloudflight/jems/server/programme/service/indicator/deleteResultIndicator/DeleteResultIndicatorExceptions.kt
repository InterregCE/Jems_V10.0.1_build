package io.cloudflight.jems.server.programme.service.indicator.deleteResultIndicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_RESULT_INDICATOR_ERROR_CODE_PREFIX = "S-IND-DRI"
private const val DELETE_RESULT_INDICATOR_ERROR_KEY_PREFIX = "use.case.delete.result.indicator"

class DeleteResultIndicatorFailed(cause: Throwable) : ApplicationException(
    code = DELETE_RESULT_INDICATOR_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_RESULT_INDICATOR_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class DeleteResultIndicatorProgrammeSetupRestrictedException : ApplicationBadRequestException(
    code = "$DELETE_RESULT_INDICATOR_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_RESULT_INDICATOR_ERROR_KEY_PREFIX.programme.setup.restricted"),
)
