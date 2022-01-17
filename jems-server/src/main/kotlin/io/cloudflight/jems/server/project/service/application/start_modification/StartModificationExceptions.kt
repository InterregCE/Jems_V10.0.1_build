package io.cloudflight.jems.server.project.service.application.start_modification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException


const val START_MODIFICATION_ERROR_CODE_PREFIX = "S-PA-SM"
const val START_MODIFICATION_TO_APPLICANT_ERROR_KEY_PREFIX = "use.case.start.modification"

class StartModificationExceptions(cause: Throwable) : ApplicationException(
    code = START_MODIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$START_MODIFICATION_TO_APPLICANT_ERROR_KEY_PREFIX.failed"), cause = cause
)
