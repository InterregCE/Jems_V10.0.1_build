package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX = "S-LPACC"
private const val LIST_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX = "use.case.list.project.audit.control.correction"

class ListAuditControlCorrectionException(cause: Throwable): ApplicationException(
    code = LIST_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PROJECT_AUDIT_CONTROL_CORRECTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

