package io.cloudflight.jems.server.user.service.user.create_user

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_USER_ERROR_CODE_PREFIX = "S-CU"
private const val CREATE_USER_ERROR_KEY_PREFIX = "use.case.create.user"

class CreateUserException(cause: Throwable) : ApplicationException(
    code = CREATE_USER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_USER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class UserRoleNotFound : ApplicationNotFoundException(
    code = "$CREATE_USER_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_USER_ERROR_KEY_PREFIX.user.role.not.found"),
)

class UserEmailAlreadyTaken : ApplicationUnprocessableException(
    code = "$CREATE_USER_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_USER_ERROR_KEY_PREFIX.email.already.in.use"),
)

class UserIdCannotBeSpecified : ApplicationUnprocessableException(
    code = "$CREATE_USER_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CREATE_USER_ERROR_KEY_PREFIX.id.forbidden"),
    message = "You cannot specify id when creating new user."
)
