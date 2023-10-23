package io.cloudflight.jems.server.project.service.auditAndControl.correction.createProjectAuditCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX = "S-CPACC"
private const val CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX = "use.case.create.project.audit.control.correction"

class CrateProjectAuditControlCorrectionException(cause: Throwable): ApplicationException(
    code = "$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class AuditControlIsInStatusClosedException: ApplicationUnprocessableException(
    code =  "$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.status.is.closed"),
)

class MaximumNumberOfCorrectionsException: ApplicationUnprocessableException(
    code =  "$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.maximum.number.reached"),
)


