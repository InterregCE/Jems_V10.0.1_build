package io.cloudflight.jems.server.project.service.auditAndControl.base.createAuditControl

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-CPAC"
private const val ERROR_KEY_PREFIX = "use.case.create.project.audit"

class CrateProjectAuditControlException(cause: Throwable): ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class MaxNumberOfAuditsReachedException: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.max.number.of.audits.reached"),
    cause = null,
)

class AuditControlClosedException: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.status.closed"),
    cause = null,
)
