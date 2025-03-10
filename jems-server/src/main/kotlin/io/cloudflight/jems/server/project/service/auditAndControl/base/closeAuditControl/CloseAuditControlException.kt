package io.cloudflight.jems.server.project.service.auditAndControl.base.closeAuditControl

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-CPAC"
private const val ERROR_KEY_PREFIX = "use.case.close.project.audit"

class CloseProjectAuditControlException(cause: Throwable): ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class AuditControlClosedException: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.status.not.ongoing"),
    cause = null
)

class CorrectionsStillOpenException: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.not.all.corrections.closed"),
    cause = null
)
