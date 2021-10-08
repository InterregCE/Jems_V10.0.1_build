package io.cloudflight.jems.server.user.service.user.activate_user

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val CONFIRM_REGISTRATION_ERROR_CODE_PREFIX = "S-CR"
private const val CONFIRM_REGISTRATION_ERROR_KEY_PREFIX = "use.case.confirm.registration"

class ActivateUserException(cause: Throwable) : ApplicationException(
    code = CONFIRM_REGISTRATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CONFIRM_REGISTRATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class LinkExpired : ApplicationAccessDeniedException(
    code = "$CONFIRM_REGISTRATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CONFIRM_REGISTRATION_ERROR_KEY_PREFIX.link.expired"),
)

class UserAlreadyActive : ApplicationBadRequestException(
    code = "$CONFIRM_REGISTRATION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CONFIRM_REGISTRATION_ERROR_KEY_PREFIX.already.active"),
)
