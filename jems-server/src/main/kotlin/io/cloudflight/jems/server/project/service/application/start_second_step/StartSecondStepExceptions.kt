package io.cloudflight.jems.server.project.service.application.start_second_step

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val START_SECOND_STEP_ERROR_CODE_PREFIX = "S-PA-SST"
const val START_SECOND_STEP_ERROR_KEY_PREFIX = "use.case.start.second.step"

class StartSecondStepException(cause: Throwable) : ApplicationException(
    code = START_SECOND_STEP_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$START_SECOND_STEP_ERROR_KEY_PREFIX.failed"), cause = cause
)
