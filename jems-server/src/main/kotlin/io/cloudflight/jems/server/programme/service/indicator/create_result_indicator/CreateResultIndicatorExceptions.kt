package io.cloudflight.jems.server.programme.service.indicator.create_result_indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.programme.service.indicator.create_output_indicator.CREATE_OUTPUT_INDICATOR_ERROR_CODE_PREFIX
import io.cloudflight.jems.server.programme.service.indicator.create_output_indicator.CREATE_OUTPUT_INDICATOR_ERROR_KEY_PREFIX

const val CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX = "S-IND-CRI"
const val CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX = "use.case.create.result.indicator"

class CreateResultIndicatorException(cause: Throwable) : ApplicationException(
    code = CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX.failed"), cause = cause
)

class InvalidIdException :
    ApplicationUnprocessableException(
        code = "$CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX-001",
        i18nMessage = I18nMessage("$CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX.id.should.not.be.set"),
        cause = null
    )

class IdentifierIsUsedException : ApplicationBadRequestException(
    code = "$CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used"),
    formErrors = mapOf("identifier" to I18nMessage("$CREATE_RESULT_INDICATOR_ERROR_KEY_PREFIX.identifier.is.used")),
    cause = null
)

class ResultIndicatorsCountExceedException : ApplicationBadRequestException(
    code = "$CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CREATE_RESULT_INDICATOR_ERROR_CODE_PREFIX.count.exceed"),
    cause = null
)
