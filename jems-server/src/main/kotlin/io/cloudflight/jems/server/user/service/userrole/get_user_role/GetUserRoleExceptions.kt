package io.cloudflight.jems.server.user.service.userrole.get_user_role

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_USER_ROLE_ERROR_CODE_PREFIX = "S-GUR"
private const val GET_USER_ROLE_ERROR_KEY_PREFIX = "use.case.get.user.role"

class GetUserRoleException(cause: Throwable) : ApplicationException(
    code = GET_USER_ROLE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_USER_ROLE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
