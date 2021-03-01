package io.cloudflight.jems.server.programme.service.indicator.create_output_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException

const val CREATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX = "S-IND-COI"
const val CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX = "use.case.create.output.indicator"

class CreateOutputIndicatorException(cause: Throwable) : ApplicationException(
    code = CREATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.failed"), cause = cause
)

class IdentifierIsUsedException : ApplicationBadRequestException(
    code = "$CREATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used"),
    formErrors = mapOf("identifier" to I18nMessage("$CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used")),
    cause = null
)

class InvalidResultIndicatorException : ApplicationBadRequestException(
    code = "$CREATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.invalid.result.indicator"),
    formErrors = mapOf("resultIndicatorId" to I18nMessage("$CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.invalid.result.indicator")),
    cause = null
)

class OutputIndicatorsCountExceedException(maxCount: Int) : ApplicationBadRequestException(
    code = "$CREATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        "$CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.count.exceed",
        mapOf("maxCount" to maxCount.toString())
    ),
    cause = null
)
