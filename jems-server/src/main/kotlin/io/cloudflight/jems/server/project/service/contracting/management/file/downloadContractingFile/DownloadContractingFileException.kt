package io.cloudflight.jems.server.project.service.contracting.management.file.downloadContractingFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DOWNLOAD_CONTRACTING_FILE_ERROR_CODE_PREFIX = "S-DCF"
private const val DOWNLOAD_CONTRACTING_FILE_ERROR_KEY_PREFIX = "use.case.download.contracting.file"

class DownloadContractingFileException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_CONTRACTING_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_CONTRACTING_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DOWNLOAD_CONTRACTING_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_CONTRACTING_FILE_ERROR_KEY_PREFIX.not.found"),
)
