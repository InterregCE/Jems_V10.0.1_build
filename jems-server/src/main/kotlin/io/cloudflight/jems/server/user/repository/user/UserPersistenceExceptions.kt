package io.cloudflight.jems.server.user.repository.user

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val USER_PERSISTENCE_ERROR_CODE = "R-UP"

class UserNotFound : ApplicationNotFoundException(
    code = "$USER_PERSISTENCE_ERROR_CODE-001",
    i18nMessage = I18nMessage("user.not.found"),
)

class UserRoleNotFound : ApplicationNotFoundException(
    code = "$USER_PERSISTENCE_ERROR_CODE-002",
    i18nMessage = I18nMessage("user.role.not.found"),
)
