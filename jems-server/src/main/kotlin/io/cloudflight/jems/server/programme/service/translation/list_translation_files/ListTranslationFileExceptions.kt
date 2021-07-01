package io.cloudflight.jems.server.programme.service.translation.list_translation_files

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val LIST_TRANSLATION_FILES_ERROR_CODE_PREFIX = "S-LTF"
const val LIST_TRANSLATION_FILES_ERROR_KEY_PREFIX = "use.case.list.translation.files"

class ListTranslationFilesException(cause: Throwable) : ApplicationException(
    code = LIST_TRANSLATION_FILES_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_TRANSLATION_FILES_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
