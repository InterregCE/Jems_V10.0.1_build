package io.cloudflight.jems.server.user.service.user.update_user_password

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_USER_PASSWORD_ERROR_CODE_PREFIX = "S-UUP"
private const val UPDATE_USER_PASSWORD_ERROR_KEY_PREFIX = "use.case.update.user.password"

class UpdateUserPasswordException(cause: Throwable) : ApplicationException(
    code = UPDATE_USER_PASSWORD_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_USER_PASSWORD_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class UserOldPasswordDoesNotMatch : ApplicationUnprocessableException(
    code = "$UPDATE_USER_PASSWORD_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_USER_PASSWORD_ERROR_KEY_PREFIX.old.password.does.not.match"),
)
