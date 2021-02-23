package io.cloudflight.jems.server.programme.service.indicator.update_output_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX = "S-IND-UOI"
const val UPDATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX = "use.case.update.output.indicator"

class UpdateOutputIndicatorException(cause: Throwable) : ApplicationException(
    code = UPDATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.failed"), cause = cause
)

class InvalidIdException :
    ApplicationUnprocessableException(
        code = "$UPDATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX-001",
        i18nMessage = I18nMessage("$UPDATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.id.should.not.be.set"),
        cause = null
    )

class IdentifierIsUsedException : ApplicationBadRequestException(
    code = "$UPDATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used"),
    formErrors = mapOf("identifier" to I18nMessage("$UPDATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used")),
    cause = null
)

class InvalidResultIndicatorException : ApplicationBadRequestException(
    code = "$UPDATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.invalid.result.indicator"),
    formErrors = mapOf("resultIndicatorId" to I18nMessage("$UPDATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.invalid.result.indicator")),
    cause = null
)
