package io.cloudflight.jems.server.project.service.auditAndControl.getProjectAuditDetails

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException


private const val GET_PROJECT_AUDIT_CONTROL_ERROR_CODE_PREFIX = "S-GPACD"
private const val GET_PROJECT_AUDIT_CONTROL_ERROR_KEY_PREFIX = "use.case.get.project.audit.details"

class GetProjectAuditDetailsException(cause: Throwable): ApplicationException(
    code = GET_PROJECT_AUDIT_CONTROL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_AUDIT_CONTROL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class AuditControlNotFound: ApplicationNotFoundException(
    code = "$GET_PROJECT_AUDIT_CONTROL_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_PROJECT_AUDIT_CONTROL_ERROR_KEY_PREFIX.not.found")
)
