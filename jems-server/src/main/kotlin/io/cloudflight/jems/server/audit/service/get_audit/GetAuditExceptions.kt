package io.cloudflight.jems.server.audit.service.get_audit

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_AUDIT_ERROR_CODE_PREFIX = "S-AUDIT"
private const val GET_AUDIT_ERROR_KEY_PREFIX = "use.case.get.audit"

class GetAuditException(cause: Throwable) : ApplicationException(
    code = GET_AUDIT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_AUDIT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
