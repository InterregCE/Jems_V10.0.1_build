package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX = "S-UPACC"
private const val UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX = "use.case.update.project.audit.control.correction"

class UpdateAuditControlCorrectionException(cause: Throwable): ApplicationException(
    code = "$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class AuditControlClosedException: ApplicationUnprocessableException(
    code =  "$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.ac.closed"),
)

class AuditControlCorrectionClosedException: ApplicationUnprocessableException(
    code =  "$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.ac.correction.closed"),
)

class CombinationOfReportAndFundIsInvalidException: ApplicationUnprocessableException(
    code =  "$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.fund.not.found"),
)

