package io.cloudflight.jems.server.project.service.report.project.annexes.download

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DOWNLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_CODE_PREFIX = "S-DPRAF"
private const val DOWNLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_KEY_PREFIX = "use.case.download.project.report.annexes.file"

class DownloadProjectReportAnnexesFileException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationUnprocessableException(
    code = "$DOWNLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_PROJECT_REPORT_ANNEXES_FILE_ERROR_KEY_PREFIX.not.found")
)
