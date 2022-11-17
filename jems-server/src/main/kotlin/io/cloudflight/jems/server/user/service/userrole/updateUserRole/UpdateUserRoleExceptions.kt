package io.cloudflight.jems.server.user.service.userrole.updateUserRole

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.user.service.model.UserRolePermission

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
    code = "$UPDATE_USER_ROLE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_USER_ROLE_ERROR_KEY_PREFIX.name.already.in.use"),
)

class UserRolePermissionCombinationInvalid(invalidCombination: Pair<UserRolePermission, UserRolePermission>) : ApplicationUnprocessableException(
    code = "$UPDATE_USER_ROLE_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        i18nKey = "$UPDATE_USER_ROLE_ERROR_KEY_PREFIX.permission.combination.invalid",
        i18nArguments = mapOf(
            "permission" to invalidCombination.first.name,
            "requires" to invalidCombination.second.name,
        ),
    ),
    message = "Permission ${invalidCombination.first} requires ${invalidCombination.second}",
)
