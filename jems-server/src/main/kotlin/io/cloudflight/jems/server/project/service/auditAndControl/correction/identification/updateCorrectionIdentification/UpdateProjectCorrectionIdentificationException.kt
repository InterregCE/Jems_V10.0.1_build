package io.cloudflight.jems.server.project.service.auditAndControl.correction.updateProjectAuditCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX = "S-UPACC"
private const val UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX = "use.case.update.project.audit.control.correction"

class UpdateProjectAuditControlCorrectionException(cause: Throwable): ApplicationException(
    code = "$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class CorrectionIsInStatusClosedException: ApplicationUnprocessableException(
    code =  "$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.status.is.closed"),
)

class PartnerReportNotValidException: ApplicationUnprocessableException(
    code =  "$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.partner.report.not.valid"),
)

