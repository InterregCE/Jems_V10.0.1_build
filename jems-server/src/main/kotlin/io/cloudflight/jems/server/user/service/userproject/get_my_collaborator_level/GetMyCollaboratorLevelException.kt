package io.cloudflight.jems.server.user.service.userproject.get_my_collaborator_level

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_MY_COLLABORATOR_LEVEL_ERROR_CODE_PREFIX = "S-GMCL"
private const val GET_MY_COLLABORATOR_LEVEL_ERROR_KEY_PREFIX = "use.case.get.my.collaborator.level"

class GetMyCollaboratorLevelException(cause: Throwable) : ApplicationException(
    code = GET_MY_COLLABORATOR_LEVEL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_MY_COLLABORATOR_LEVEL_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
