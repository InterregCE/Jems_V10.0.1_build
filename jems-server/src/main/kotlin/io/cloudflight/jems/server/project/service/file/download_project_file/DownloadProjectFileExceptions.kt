package io.cloudflight.jems.server.project.service.file.download_project_file

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DOWNLOAD_PROJECT_FILE_ERROR_CODE_PREFIX = "S-DPF"
private const val DOWNLOAD_PROJECT_FILE_ERROR_KEY_PREFIX = "use.case.download.project.file"

class DownloadProjectFileExceptions(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_PROJECT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_PROJECT_FILE_ERROR_KEY_PREFIX.failed"), cause = cause
)
