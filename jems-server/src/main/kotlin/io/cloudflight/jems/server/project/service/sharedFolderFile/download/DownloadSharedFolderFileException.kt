package io.cloudflight.jems.server.project.service.sharedFolderFile.download

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DOWNLOAD_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX = "S-DSFF"
private const val DOWNLOAD_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX = "use.case.download.shared.folder.file"

class DownloadSharedFolderFileException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationUnprocessableException(
    code = "$DOWNLOAD_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX.not.found")
)
