package io.cloudflight.jems.server.project.service.auditAndControl.file.updateDescription

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val UPDATE_DESCRIPTION_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX = "S-UDPACF"
private const val UPDATE_DESCRIPTION_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX = "use.case.update.description.project.audit.control.file"

class UpdateDescriptionAuditControlFileException(cause: Throwable) : ApplicationException(
    code = UPDATE_DESCRIPTION_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_DESCRIPTION_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$UPDATE_DESCRIPTION_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_DESCRIPTION_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX.not.found"),
)
