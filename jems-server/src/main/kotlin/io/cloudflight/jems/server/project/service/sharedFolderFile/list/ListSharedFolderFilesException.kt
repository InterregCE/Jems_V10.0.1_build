package io.cloudflight.jems.server.project.service.sharedFolderFile.list

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_SHARED_FOLDER_FILES_ERROR_CODE_PREFIX = "S-LSFF"
private const val LIST_SHARED_FOLDER_FILES_ERROR_KEY_PREFIX = "use.case.list.shared.folder.files"

class ListSharedFolderFilesException(cause: Throwable) : ApplicationException(
    code = LIST_SHARED_FOLDER_FILES_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_SHARED_FOLDER_FILES_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
