package io.cloudflight.jems.server.project.service.auditAndControl.file.upload

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX = "S-UPACF"
private const val UPLOAD_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX = "use.case.upload.project.audit.control.file"

class UploadAuditControlFileException(cause: Throwable) : ApplicationException(
    code = UPLOAD_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileAlreadyExists(fileName: String) : ApplicationUnprocessableException(
    code = "$UPLOAD_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        i18nKey = "$UPLOAD_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX.file.already.exists",
        i18nArguments = mapOf("fileName" to fileName),
    ),
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_PROJECT_AUDIT_CONTROL_FILE_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPLOAD_PROJECT_AUDIT_CONTROL_FILE_ERROR_KEY_PREFIX.type.not.supported")
)
