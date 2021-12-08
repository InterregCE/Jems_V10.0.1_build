package io.cloudflight.jems.server.user.service.userproject.get_user_collaborators_assigned_to_projects

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_USER_COLLABORATORS_WITH_PROJECT_ERROR_CODE_PREFIX = "S-GUCP"
private const val GET_USER_COLLABORATORS_WITH_PROJECT_ERROR_KEY_PREFIX = "use.case.get.user.collaborators.with.project"

class GetUserCollaboratorsAssignedToProjectsException(cause: Throwable) : ApplicationException(
    code = GET_USER_COLLABORATORS_WITH_PROJECT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_USER_COLLABORATORS_WITH_PROJECT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
