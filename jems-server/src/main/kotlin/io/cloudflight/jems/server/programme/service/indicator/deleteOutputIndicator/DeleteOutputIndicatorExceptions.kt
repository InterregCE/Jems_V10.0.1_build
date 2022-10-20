package io.cloudflight.jems.server.programme.service.indicator.deleteOutputIndicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX = "S-IND-DOI"
private const val DELETE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX = "use.case.delete.output.indicator"

class OutputIndicatorDeletionFailed(cause: Throwable) : ApplicationException(
    code = DELETE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class OutputIndicatorDeletionWhenProgrammeSetupRestricted : ApplicationBadRequestException(
    code = "$DELETE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.programme.setup.restricted"),
)
