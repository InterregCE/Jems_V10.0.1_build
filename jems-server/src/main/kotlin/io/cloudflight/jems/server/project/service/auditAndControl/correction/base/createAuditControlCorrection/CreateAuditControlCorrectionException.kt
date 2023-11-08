package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.createAuditControlCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX = "S-CPACC"
private const val CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX = "use.case.create.project.audit.control.correction"

class CreateAuditControlCorrectionException(cause: Throwable): ApplicationException(
    code = "$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaximumNumberOfCorrectionsException: ApplicationUnprocessableException(
    code =  "$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.maximum.number.reached"),
)

class AuditControlClosedException: ApplicationUnprocessableException(
    code =  "$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CREATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.ac.closed"),
)
