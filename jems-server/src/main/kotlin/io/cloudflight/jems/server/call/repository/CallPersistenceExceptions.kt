package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val CALL_PERSISTENCE_ERROR_CODE = "R-CP"

class CallNotFound : ApplicationNotFoundException(
    code = "$CALL_PERSISTENCE_ERROR_CODE-001",
    i18nMessage = I18nMessage("programme.specific.objective.not.found"), cause = null
)

class ApplicationFormConfigurationNotFound : ApplicationNotFoundException(
    code = "$CALL_PERSISTENCE_ERROR_CODE-002",
    i18nMessage = I18nMessage("application.form.configuration.not.found"), cause = null
)

