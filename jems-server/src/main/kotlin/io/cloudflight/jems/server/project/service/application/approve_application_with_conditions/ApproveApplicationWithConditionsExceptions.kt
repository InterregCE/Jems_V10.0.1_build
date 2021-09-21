package io.cloudflight.jems.server.project.service.application.approve_application_with_conditions

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val APPROVE_APPLICATION_WITH_CONDITIONS_ERROR_CODE_PREFIX = "S-PA-AAWC"
private const val APPROVE_APPLICATION_WITH_CONDITIONS_ERROR_KEY_PREFIX = "use.case.approve.application.with.condition"

class ApproveApplicationWithConditionsException(cause: Throwable) : ApplicationException(
    code = APPROVE_APPLICATION_WITH_CONDITIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$APPROVE_APPLICATION_WITH_CONDITIONS_ERROR_KEY_PREFIX.failed"), cause = cause
)
