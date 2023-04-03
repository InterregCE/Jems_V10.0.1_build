package io.cloudflight.jems.server.project.service.sharedFolderFile.description

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val SET_DESCRIPTION_TO_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX = "S-SDTSFF"
private const val SET_DESCRIPTION_TO_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX = "use.case.set.description.to.shared.folder.file"

class SetDescriptionToSharedFolderFileException(cause: Throwable) : ApplicationException(
    code = SET_DESCRIPTION_TO_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationUnprocessableException(
    code = "$SET_DESCRIPTION_TO_SHARED_FOLDER_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_SHARED_FOLDER_FILE_ERROR_KEY_PREFIX.not.found")
)

