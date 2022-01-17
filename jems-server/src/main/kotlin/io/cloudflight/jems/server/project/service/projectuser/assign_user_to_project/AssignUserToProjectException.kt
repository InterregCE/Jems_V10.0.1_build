package io.cloudflight.jems.server.project.service.projectuser.assign_user_to_project

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val ASSIGN_USER_TO_PROJECT_ERROR_CODE_PREFIX = "S-AUTP"
private const val ASSIGN_USER_TO_PROJECT_ERROR_KEY_PREFIX = "use.case.assign.user.to.project"

class AssignUserToProjectException(cause: Throwable) : ApplicationException(
    code = ASSIGN_USER_TO_PROJECT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ASSIGN_USER_TO_PROJECT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
