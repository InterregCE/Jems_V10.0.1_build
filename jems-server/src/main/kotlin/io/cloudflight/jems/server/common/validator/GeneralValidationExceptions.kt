package io.cloudflight.jems.server.common.validator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException

const val GENERAL_VALIDATION_ERROR_CODE_PREFIX = "S-INP-ERR"

class AppInputValidationException(formErrors: Map<String, I18nMessage>) : ApplicationBadRequestException(
    code = "$GENERAL_VALIDATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("common.error.input.invalid"),
    formErrors = formErrors,
    cause = null,
)
