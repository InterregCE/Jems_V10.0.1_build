package io.cloudflight.jems.server.authentication.service.resetPasswordByToken

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val RESET_PASSWORD_BY_TOKEN_ERROR_CODE_PREFIX = "S-RPT"
const val RESET_PASSWORD_BY_TOKEN_ERROR_KEY_PREFIX = "use.case.reset.password.by.token"

class ResetPasswordByTokenException(cause: Throwable) : ApplicationException(
    code = RESET_PASSWORD_BY_TOKEN_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$RESET_PASSWORD_BY_TOKEN_ERROR_KEY_PREFIX.failed"), cause = cause
)

class ResetPasswordTokenIsExpiredException : ApplicationUnprocessableException(
    code = "$RESET_PASSWORD_BY_TOKEN_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$RESET_PASSWORD_BY_TOKEN_ERROR_KEY_PREFIX.expired.token")
)
class ResetPasswordTokenIsInvalidException : ApplicationUnprocessableException(
    code = "$RESET_PASSWORD_BY_TOKEN_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$RESET_PASSWORD_BY_TOKEN_ERROR_KEY_PREFIX.invalid.token")
)
class ResetPasswordTokenFormatIsInvalidException : ApplicationUnprocessableException(
    code = "$RESET_PASSWORD_BY_TOKEN_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$RESET_PASSWORD_BY_TOKEN_ERROR_KEY_PREFIX.invalid.token.format")
)
