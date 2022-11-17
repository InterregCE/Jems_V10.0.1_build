package io.cloudflight.jems.server.user.service.user.register_user

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val REGISTER_USER_ERROR_CODE_PREFIX = "S-RU"
private const val REGISTER_USER_ERROR_KEY_PREFIX = "use.case.register.user"

class RegisterUserException(cause: Throwable) : ApplicationException(
    code = REGISTER_USER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$REGISTER_USER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class DefaultUserRoleNotFound : ApplicationNotFoundException(
    code = "$REGISTER_USER_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$REGISTER_USER_ERROR_KEY_PREFIX.user.role.not.found"),
)

class UserEmailAlreadyTaken : ApplicationUnprocessableException(
    code = "$REGISTER_USER_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$REGISTER_USER_ERROR_KEY_PREFIX.email.already.in.use"),
)

class CaptchaNotValid : ApplicationUnprocessableException(
    code = "$REGISTER_USER_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$REGISTER_USER_ERROR_KEY_PREFIX.invalid.captcha"),
)
