package io.cloudflight.jems.server.user.service.userproject.get_users_assigned_to_projects

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_USERS_WITH_PROJECT_ERROR_CODE_PREFIX = "S-GUP"
private const val GET_USERS_WITH_PROJECT_ERROR_KEY_PREFIX = "use.case.get.users.with.project"

class GetUsersAssignedToProjectsException(cause: Throwable) : ApplicationException(
    code = GET_USERS_WITH_PROJECT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_USERS_WITH_PROJECT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
