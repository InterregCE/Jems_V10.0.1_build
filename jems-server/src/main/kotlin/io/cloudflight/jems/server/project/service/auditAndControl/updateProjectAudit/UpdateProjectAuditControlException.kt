package io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROJECT_AUDIT_CONTROL_ERROR_CODE_PREFIX = "S-UPAC"
private const val UPDATE__PROJECT_AUDIT_CONTROL_ERROR_KEY_PREFIX = "use.case.update.project.audit"


class UpdateProjectAuditControlException (cause: Throwable): ApplicationException(
    code = UPDATE_PROJECT_AUDIT_CONTROL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE__PROJECT_AUDIT_CONTROL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)