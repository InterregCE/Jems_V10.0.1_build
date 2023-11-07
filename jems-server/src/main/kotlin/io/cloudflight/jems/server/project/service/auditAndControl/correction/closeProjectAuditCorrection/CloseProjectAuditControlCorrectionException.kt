package io.cloudflight.jems.server.project.service.auditAndControl.correction.closeProjectAuditCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CLOSE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX = "S-CPACC"
private const val CLOSE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX = "use.case.close.project.audit.control.correction"

class CloseProjectAuditControlCorrectionException(cause: Throwable): ApplicationException(
    code = "$CLOSE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CLOSE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ProjectCorrectionIsInStatusClosedException: ApplicationUnprocessableException(
    code =  "$CLOSE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CLOSE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.status.is.closed"),
)

class PartnerOrReportOrFundNotSelectedException: ApplicationUnprocessableException(
    code =  "$CLOSE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CLOSE_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.partner.or.report.or.fund.not.selected"),
)



