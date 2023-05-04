package io.cloudflight.jems.server.call.service.translation.getTranslation

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val ERROR_CODE_PREFIX = "S-GCTF"
private const val ERROR_KEY_PREFIX = "use.case.get.call.translation.file"

class GetTranslationException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
