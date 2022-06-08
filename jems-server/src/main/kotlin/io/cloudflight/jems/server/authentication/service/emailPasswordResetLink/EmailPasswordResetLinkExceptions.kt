package io.cloudflight.jems.server.authentication.service.emailPasswordResetLink

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val SEND_PASSWORD_RESET_LINK_TO_EMAIL_ERROR_CODE_PREFIX = "S-SRPL"
const val SEND_PASSWORD_RESET_LINK_TO_EMAIL_ERROR_KEY_PREFIX = "use.case.send.password.reset.link.to.email"

class EmailPasswordResetLinkException(cause: Throwable) : ApplicationException(
    code = SEND_PASSWORD_RESET_LINK_TO_EMAIL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SEND_PASSWORD_RESET_LINK_TO_EMAIL_ERROR_KEY_PREFIX.failed"), cause = cause
)
