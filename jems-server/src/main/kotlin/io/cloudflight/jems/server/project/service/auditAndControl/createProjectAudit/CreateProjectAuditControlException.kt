package io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_AUDIT_CONTROL_ERROR_CODE_PREFIX = "S-CPAC"
private const val CREATE_AUDIT_CONTROL_ERROR_KEY_PREFIX = "use.case.create.project.audit"


class CrateProjectAuditControlException(cause: Throwable): ApplicationException(
    code = CREATE_AUDIT_CONTROL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_AUDIT_CONTROL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaxNumberOfAuditsReachedException: ApplicationUnprocessableException(
    code = "$CREATE_AUDIT_CONTROL_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_AUDIT_CONTROL_ERROR_KEY_PREFIX.max.number.of.audits.reached"),
    cause = null
    )

class AuditControlClosedException: ApplicationUnprocessableException(
    code = "$CREATE_AUDIT_CONTROL_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_AUDIT_CONTROL_ERROR_KEY_PREFIX.status.closed"),
    cause = null
)