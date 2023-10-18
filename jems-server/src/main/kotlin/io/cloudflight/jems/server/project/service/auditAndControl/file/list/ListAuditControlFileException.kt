package io.cloudflight.jems.server.project.service.auditAndControl.file.list

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException


private const val LIST_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX = "S-LPACF"
private const val LIST_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX = "use.case.list.project.audit.control.file"

class ListAuditControlFileException(cause: Throwable): ApplicationException (
    code = LIST_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$LIST_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$LIST_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX.not.found"),
)
