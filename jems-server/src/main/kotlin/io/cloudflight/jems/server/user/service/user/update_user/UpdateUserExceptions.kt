package io.cloudflight.jems.server.user.service.user.update_user

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_USER_ERROR_CODE_PREFIX = "S-UU"
private const val UPDATE_USER_ERROR_KEY_PREFIX = "use.case.update.user"

class UpdateUserException(cause: Throwable) : ApplicationException(
    code = UPDATE_USER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_USER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class UserRoleNotFound : ApplicationNotFoundException(
    code = "$UPDATE_USER_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_USER_ERROR_KEY_PREFIX.user.role.not.found"),
)

class UserEmailAlreadyTaken : ApplicationUnprocessableException(
    code = "$UPDATE_USER_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_USER_ERROR_KEY_PREFIX.email.already.in.use"),
)
