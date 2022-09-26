package io.cloudflight.jems.server.project.service.report.partner.file.control.downloadControlReportFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DOWNLOAD_CONTROL_REPORT_FILE_ERROR_CODE_PREFIX = "S-DPPCRF"
private const val DOWNLOAD_CONTROL_REPORT_FILE_ERROR_KEY_PREFIX = "use.case.download.project.partner.control.report.file"

class DownloadControlReportFileException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_CONTROL_REPORT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_CONTROL_REPORT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DOWNLOAD_CONTROL_REPORT_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_CONTROL_REPORT_FILE_ERROR_KEY_PREFIX.not.found"),
)
