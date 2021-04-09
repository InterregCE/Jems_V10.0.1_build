package io.cloudflight.jems.server.project.service.application.revert_application_decision

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val REVERT_APPLICATION_DECISION_ERROR_CODE_PREFIX = "S-PA-RAD"
const val REVERT_APPLICATION_DECISION_ERROR_KEY_PREFIX = "use.case.revert.application.decision"

class RevertApplicationDecisionException(cause: Throwable) : ApplicationException(
    code = REVERT_APPLICATION_DECISION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$REVERT_APPLICATION_DECISION_ERROR_KEY_PREFIX.failed"), cause = cause
)
