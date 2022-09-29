package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadInternalFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DOWNLOAD_INTERNAL_FILE_ERROR_CODE_PREFIX = "S-DCIF"
private const val DOWNLOAD_INTERNAL_FILE_ERROR_KEY_PREFIX = "use.case.download.internal.file"

class DownloadInternalFileException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_INTERNAL_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_INTERNAL_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
