package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val SUBMIT_APPLICATION_ERROR_CODE_PREFIX = "S-PA-SA"
const val SUBMIT_APPLICATION_ERROR_KEY_PREFIX = "use.case.submit.application"

class SubmitApplicationException(cause: Throwable) : ApplicationException(
    code = SUBMIT_APPLICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SUBMIT_APPLICATION_ERROR_KEY_PREFIX.failed"), cause = cause
)

class SubmitApplicationPreConditionCheckFailedException : ApplicationUnprocessableException(
    code = "$SUBMIT_APPLICATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SUBMIT_APPLICATION_ERROR_KEY_PREFIX.pre.condition.check.failed")
)
