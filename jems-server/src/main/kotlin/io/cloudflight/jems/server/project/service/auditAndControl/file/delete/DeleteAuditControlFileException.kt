package io.cloudflight.jems.server.project.service.auditAndControl.file.delete

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DELETE_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX = "S-DPACF"
private const val DELETE_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX = "use.case.delete.project.audit.control.file"

class DeleteAuditControlFileException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DELETE_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX.not.found"),
)
