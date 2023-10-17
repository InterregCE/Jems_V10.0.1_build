package io.cloudflight.jems.server.project.service.auditAndControl.correction.deleteProjectAuditCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX = "S-DPACC"
private const val DELETE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX = "use.case.delete.project.audit.control.correction"

class DeleteProjectAuditControlCorrectionException(cause: Throwable): ApplicationException(
    code = "$DELETE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class AuditControlIsNotLastCorrectionException: ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$DELETE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.is.not.last.correction"),
)

class AuditControlNoCorrectionSavedException: ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$DELETE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.no.corrections.saved"),
)
