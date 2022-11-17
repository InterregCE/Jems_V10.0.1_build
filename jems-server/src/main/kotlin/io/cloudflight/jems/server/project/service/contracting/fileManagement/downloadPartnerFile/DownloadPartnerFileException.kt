package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadPartnerFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DOWNLOAD_PARTNER_FILE_ERROR_CODE_PREFIX = "S-DPF"
private const val DOWNLOAD_PARTNER_FILE_ERROR_KEY_PREFIX = "use.case.download.partner.file"

class DownloadPartnerFileException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_PARTNER_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_PARTNER_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

