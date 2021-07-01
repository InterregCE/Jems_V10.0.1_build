package io.cloudflight.jems.server.programme.service.translation.upload_translation_file

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val UPLOAD_TRANSLATION_FILE_ERROR_CODE_PREFIX = "S-UTF"
const val UPLOAD_TRANSLATION_FILE_ERROR_KEY_PREFIX = "use.case.upload.translation.file"

class UploadTranslationFileFailed(cause: Throwable) : ApplicationException(
    code = UPLOAD_TRANSLATION_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_TRANSLATION_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
