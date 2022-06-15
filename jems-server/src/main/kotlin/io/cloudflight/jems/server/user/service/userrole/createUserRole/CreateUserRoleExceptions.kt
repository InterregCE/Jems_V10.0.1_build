package io.cloudflight.jems.server.user.service.userrole.createUserRole

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.user.service.model.UserRolePermission

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

class UserRolePermissionCombinationInvalid(invalidCombination: Pair<UserRolePermission, UserRolePermission>) : ApplicationUnprocessableException(
    code = "$CREATE_USER_ROLE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        i18nKey = "$CREATE_USER_ROLE_ERROR_KEY_PREFIX.permission.combination.invalid",
        i18nArguments = mapOf(
            "permission" to invalidCombination.first.name,
            "requires" to invalidCombination.second.name,
        ),
    ),
    message = "Permission ${invalidCombination.first} requires ${invalidCombination.second}",
)
