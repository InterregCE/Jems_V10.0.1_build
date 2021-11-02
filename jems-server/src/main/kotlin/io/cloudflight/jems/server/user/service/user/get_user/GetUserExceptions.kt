package io.cloudflight.jems.server.user.service.user.get_user

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_USER_ERROR_CODE_PREFIX = "S-GU"
private const val GET_USER_ERROR_KEY_PREFIX = "use.case.get.user"

class GetUserException(cause: Throwable) : ApplicationException(
    code = GET_USER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_USER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

private const val GET_USERS_FILTERED_BY_PERMISSIONS_ERROR_CODE_PREFIX = "S-GUFBP"
private const val GET_USERS_FILTERED_BY_PERMISSIONS_ERROR_KEY_PREFIX = "use.case.get.users.filtered.by.permissions"

class GetUsersFilteredByPermissionsException(cause: Throwable) : ApplicationException(
    code = GET_USERS_FILTERED_BY_PERMISSIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_USERS_FILTERED_BY_PERMISSIONS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
