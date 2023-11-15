package io.cloudflight.jems.server.project.service.auditAndControl.base.reopenAuditControl

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-CPAR"
private const val ERROR_KEY_PREFIX = "use.case.reopen.project.audit"

class ReopenProjectAuditControlException(cause: Throwable): ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class AuditControlNotClosedException: ApplicationUnprocessableException(
    code = "${ERROR_CODE_PREFIX}-001",
    i18nMessage = I18nMessage("${ERROR_KEY_PREFIX}.status.not.closed"),
    cause = null
)
