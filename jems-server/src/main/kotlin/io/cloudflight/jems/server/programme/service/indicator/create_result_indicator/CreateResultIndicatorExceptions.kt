package io.cloudflight.jems.server.programme.service.indicator.create_result_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX = "S-IND-CRI"
const val CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX = "use.case.create.result.indicator"

class CreateResultIndicatorException(cause: Throwable) : ApplicationException(
    code = CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX.failed"), cause = cause
)


class IdentifierIsUsedException : ApplicationBadRequestException(
    code = "$CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used"),
    formErrors = mapOf("identifier" to I18nMessage("$CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used")),
    cause = null
)

class ResultIndicatorsCountExceedException(maxCount: Int) : ApplicationBadRequestException(
    code = "$CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        "$CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX.count.exceed",
        mapOf("maxCount" to maxCount.toString())
    ),
    cause = null
)
