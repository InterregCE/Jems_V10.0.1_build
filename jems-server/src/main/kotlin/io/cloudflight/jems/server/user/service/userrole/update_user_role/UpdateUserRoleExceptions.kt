package io.cloudflight.jems.server.user.service.userrole.update_user_role

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_USER_ROLE_ERROR_CODE_PREFIX = "S-UUR"
private const val UPDATE_USER_ROLE_ERROR_KEY_PREFIX = "use.case.update.user.role"

class UpdateUserRoleException(cause: Throwable) : ApplicationException(
    code = UPDATE_USER_ROLE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_USER_ROLE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class UserRoleNotFound : ApplicationNotFoundException(
    code = "$UPDATE_USER_ROLE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_USER_ROLE_ERROR_KEY_PREFIX.not.found"),
)

class UserRoleNameAlreadyTaken : ApplicationUnprocessableException(
    code = "$UPDATE_USER_ROLE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_USER_ROLE_ERROR_KEY_PREFIX.name.already.in.use"),
)

