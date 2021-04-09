package io.cloudflight.jems.server.project.service.application.return_application_to_applicant

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val RETURN_APPLICATION_TO_APPLICANT_ERROR_CODE_PREFIX = "S-PA-RATA"
const val RETURN_APPLICATION_TO_APPLICANT_ERROR_KEY_PREFIX = "use.case.return.application.to.applicant"

class ReturnApplicationToApplicantException(cause: Throwable) : ApplicationException(
    code = RETURN_APPLICATION_TO_APPLICANT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$RETURN_APPLICATION_TO_APPLICANT_ERROR_KEY_PREFIX.failed"), cause = cause
)
