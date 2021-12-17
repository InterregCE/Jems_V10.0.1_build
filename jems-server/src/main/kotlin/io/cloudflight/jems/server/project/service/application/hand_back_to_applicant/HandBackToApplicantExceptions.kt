package io.cloudflight.jems.server.project.service.application.hand_back_to_applicant

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val HAND_BACK_TO_APPLICANT_ERROR_CODE_PREFIX = "S-PA-HA"
private const val HAND_BACK_TO_APPLICANT_ERROR_KEY_PREFIX = "use.case.hand.back.to.applicant"

class HandBackToApplicantException(cause: Throwable) : ApplicationException(
    code = HAND_BACK_TO_APPLICANT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$HAND_BACK_TO_APPLICANT_ERROR_KEY_PREFIX.failed"), cause = cause
)

class HandBackToApplicantAccessDeniedException : ApplicationAccessDeniedException(
    code = "$HAND_BACK_TO_APPLICANT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$HAND_BACK_TO_APPLICANT_ERROR_KEY_PREFIX.access.denied")
)
