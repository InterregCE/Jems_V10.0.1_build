package io.cloudflight.jems.server.project.service.auditAndControl.listProjectAudits

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_PROJECT_AUDIT_CONTROL_ERROR_CODE_PREFIX = "S-LPAC"
private const val LIST_PROJECT_AUDIT_CONTROL_ERROR_KEY_PREFIX = "use.case.list.project.audit"


class ListProjectAuditControlException (cause: Throwable): ApplicationException(
    code = LIST_PROJECT_AUDIT_CONTROL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PROJECT_AUDIT_CONTROL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)