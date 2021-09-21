package io.cloudflight.jems.server.project.service.file.upload_project_file

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_FILE_ERROR_CODE_PREFIX = "S-UPF"
private const val UPLOAD_FILE_ERROR_KEY_PREFIX = "use.case.upload.project.file"

class UploadFileExceptions(cause: Throwable) : ApplicationException(
    code = UPLOAD_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_FILE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class UploadInCategoryIsNotAllowedExceptions : ApplicationUnprocessableException(
    code = "$UPLOAD_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPLOAD_FILE_ERROR_KEY_PREFIX.in.category.is.not.allowed")
)

