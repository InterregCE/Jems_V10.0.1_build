package io.cloudflight.jems.server.project.service.application.approve_application

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val APPROVE_APPLICATION_ERROR_CODE_PREFIX = "S-PA-AA"
const val APPROVE_APPLICATION_ERROR_KEY_PREFIX = "use.case.approve.application"

class ApproveApplicationException(cause: Throwable) : ApplicationException(
    code = APPROVE_APPLICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$APPROVE_APPLICATION_ERROR_KEY_PREFIX.failed"), cause = cause
)
