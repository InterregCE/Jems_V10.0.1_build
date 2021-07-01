package io.cloudflight.jems.server.programme.service.translation.download_translation_file

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val DOWNLOAD_TRANSLATION_FILE_ERROR_CODE_PREFIX = "S-DTF"
const val DOWNLOAD_TRANSLATION_FILE_ERROR_KEY_PREFIX = "use.case.download.translation.file"

class DownloadTranslationFileFailed(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_TRANSLATION_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_TRANSLATION_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
