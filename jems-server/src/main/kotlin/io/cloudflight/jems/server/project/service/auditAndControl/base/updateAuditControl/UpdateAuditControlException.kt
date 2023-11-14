package io.cloudflight.jems.server.project.service.auditAndControl.base.updateAuditControl

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val ERROR_CODE_PREFIX = "S-UPAC"
private const val ERROR_KEY_PREFIX = "use.case.update.project.audit"


class UpdateAuditControlException (cause: Throwable): ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
