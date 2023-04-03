package io.cloudflight.jems.server.project.service.sharedFolderFile.upload

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX = "S-USFF"
private const val UPLOAD_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX = "use.case.upload.shared.folder.file"

class UploadFileToSharedFolderException(cause: Throwable) : ApplicationException(
    code = UPLOAD_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileAlreadyExists : ApplicationUnprocessableException(
    code = "$UPLOAD_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPLOAD_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX.already.exists")
)

