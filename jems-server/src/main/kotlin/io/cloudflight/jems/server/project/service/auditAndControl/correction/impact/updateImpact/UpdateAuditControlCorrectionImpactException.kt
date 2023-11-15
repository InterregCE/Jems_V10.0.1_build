package io.cloudflight.jems.server.project.service.auditAndControl.correction.impact.updateImpact

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-UACCI"
private const val ERROR_KEY_PREFIX = "use.case.update.audit.control.correction.impact"

class UpdateAuditControlCorrectionImpactException(cause: Throwable): ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class AuditControlClosedException: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.status.not.ongoing"),
)

class AuditControlCorrectionClosedException: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.ac.correction.closed"),
)
