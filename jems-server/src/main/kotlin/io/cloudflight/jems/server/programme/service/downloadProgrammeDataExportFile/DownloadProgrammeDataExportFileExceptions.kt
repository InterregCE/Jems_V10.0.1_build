package io.cloudflight.jems.server.programme.service.downloadProgrammeDataExportFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DOWNLOAD_PROGRAMME_DATA_EXPORT_FILE_ERROR_CODE_PREFIX = "S-DEF"
private const val DOWNLOAD_PROGRAMME_DATA_EXPORT_FILE_ERROR_KEY_PREFIX = "use.case.download.programme.data.export.file"

class DownloadProgrammeDataExportFileException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_PROGRAMME_DATA_EXPORT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_PROGRAMME_DATA_EXPORT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
class PluginKeyIsInvalidException : ApplicationUnprocessableException(
    code = "$DOWNLOAD_PROGRAMME_DATA_EXPORT_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_PROGRAMME_DATA_EXPORT_FILE_ERROR_KEY_PREFIX.invalid.plugin.key")
)
class ExportFileIsNotReadyException : ApplicationUnprocessableException(
    code = "$DOWNLOAD_PROGRAMME_DATA_EXPORT_FILE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$DOWNLOAD_PROGRAMME_DATA_EXPORT_FILE_ERROR_KEY_PREFIX.is.not.ready")
)
