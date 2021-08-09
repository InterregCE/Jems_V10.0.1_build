package io.cloudflight.jems.server.project.service.file.set_project_file_description

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val SET_PROJECT_FILE_DESCRIPTION_ERROR_CODE_PREFIX = "S-SPFD"
private const val SET_PROJECT_FILE_DESCRIPTION_ERROR_KEY_PREFIX = "use.case.set.project.file.description"

class SetProjectFileDescriptionExceptions(cause: Throwable) : ApplicationException(
    code = SET_PROJECT_FILE_DESCRIPTION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_PROJECT_FILE_DESCRIPTION_ERROR_KEY_PREFIX.failed"), cause = cause
)
