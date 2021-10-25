package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val USER_ROLE_PERSISTENCE_ERROR_CODE = "R-URP"

class PermissionFilterEmpty : ApplicationBadRequestException(
    code = "$USER_ROLE_PERSISTENCE_ERROR_CODE-001",
    i18nMessage = I18nMessage("cannot.filter.roles.by.permissions.without.permissions.provided"),
)

