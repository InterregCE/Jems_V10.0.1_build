package io.cloudflight.jems.server.call.service.translation.downloadTranslationFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val ERROR_CODE_PREFIX = "S-DCTF"
private const val ERROR_KEY_PREFIX = "use.case.download.call.translation.file"

class DownloadCallTranslationFileException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class FileNotFound : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.not.found"),
)
