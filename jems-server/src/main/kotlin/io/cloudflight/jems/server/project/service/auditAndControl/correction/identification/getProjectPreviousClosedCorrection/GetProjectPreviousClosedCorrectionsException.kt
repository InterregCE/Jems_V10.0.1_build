package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_AUDIT_CONTROL_PAST_CORRECTIONS_ERROR_CODE_PREFIX = "S-GPACPC"
private const val GET_PROJECT_AUDIT_CONTROL_PAST_CORRECTIONS_PREFIX = "use.case.get.project.audit.control.past.corrections"

class GetProjectPreviousClosedCorrectionsException(cause: Throwable): ApplicationException(
    code = GET_PROJECT_AUDIT_CONTROL_PAST_CORRECTIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_AUDIT_CONTROL_PAST_CORRECTIONS_PREFIX.failed"),
    cause = cause
)

