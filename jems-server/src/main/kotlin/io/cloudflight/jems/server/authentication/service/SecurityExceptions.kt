package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import org.springframework.security.core.AuthenticationException

const val SECURITY_ERROR_CODE_PREFIX = "S-SEC"
const val SECURITY_ERROR_KEY_PREFIX = "cross.cutting.security"


class CurrentUseIdIsNullException : ApplicationException(
    code = "$SECURITY_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SECURITY_ERROR_KEY_PREFIX.current.user.id.is.null"),
    cause = null
)

class LoginBlockedException(val email: String, val maxNumberOfLoginAttempts: Short, val timeToUnblockLoginInMinutes: Long) : AuthenticationException(
    "login is blocked for '$email' due to more than $maxNumberOfLoginAttempts failed login attempts." +
        " It will be unblocked in $timeToUnblockLoginInMinutes minutes"
)
