package io.cloudflight.jems.server.user.service.userrole.create_user_role

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_USER_ROLE_ERROR_CODE_PREFIX = "S-CUR"
private const val CREATE_USER_ROLE_ERROR_KEY_PREFIX = "use.case.create.user.role"

class CreateUserRoleException(cause: Throwable) : ApplicationException(
    code = CREATE_USER_ROLE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_USER_ROLE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class UserRoleNameAlreadyTaken : ApplicationUnprocessableException(
    code = "$CREATE_USER_ROLE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_USER_ROLE_ERROR_KEY_PREFIX.name.already.in.use"),
)
