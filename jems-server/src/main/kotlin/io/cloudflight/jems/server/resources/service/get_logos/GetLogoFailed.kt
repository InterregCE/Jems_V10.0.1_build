package io.cloudflight.jems.server.resources.service.get_logos

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_LOGO_ERROR_CODE_PREFIX = "S-GL"
const val GET_LOGO_ERROR_KEY_PREFIX = "use.case.get.logo.file"

class GetLogoFailed(cause: Throwable) : ApplicationException(
    code = GET_LOGO_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_LOGO_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
