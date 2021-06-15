package io.cloudflight.jems.server.project.service.application.refuse_application

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val REFUSE_APPLICATION_ERROR_CODE_PREFIX = "S-PA-RA"
private const val REFUSE_APPLICATION_ERROR_KEY_PREFIX = "use.case.refuse.application"

class RefuseApplicationException(cause: Throwable) : ApplicationException(
    code = REFUSE_APPLICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$REFUSE_APPLICATION_ERROR_KEY_PREFIX.failed"), cause = cause
)

class QualityAssessmentMissing : ApplicationUnprocessableException(
    code = "$REFUSE_APPLICATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$REFUSE_APPLICATION_ERROR_KEY_PREFIX.missing.quality.assessment"),
)
