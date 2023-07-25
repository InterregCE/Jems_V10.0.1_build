package io.cloudflight.jems.server.project.service.sharedFolderFile.delete

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX = "S-DSFF"
private const val DELETE_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX = "use.case.delete.shared.folder.file"

class DeleteFileFromSharedFolderException(cause: Throwable) : ApplicationException(
    code = DELETE_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DELETE_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX.not.found")
)
