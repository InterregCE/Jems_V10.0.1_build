package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadContractFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DOWNLOAD_CONTRACT_FILE_ERROR_CODE_PREFIX = "S-DCF"
private const val DOWNLOAD_CONTRACT_FILE_ERROR_KEY_PREFIX = "use.case.download.contract.file"

class DownloadContractFileException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_CONTRACT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_CONTRACT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
