package io.cloudflight.jems.server.project.service.application.refuse_application

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val REFUSE_APPLICATION_ERROR_CODE_PREFIX = "S-PA-RA"
const val REFUSE_APPLICATION_ERROR_KEY_PREFIX = "use.case.refuse.application"

class RefuseApplicationException(cause: Throwable) : ApplicationException(
    code = REFUSE_APPLICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$REFUSE_APPLICATION_ERROR_KEY_PREFIX.failed"), cause = cause
)
