package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val PROJECT_ERROR_CODE_PREFIX = "P-PA"
const val PROJECT_ERROR_KEY_PREFIX = "project"

class PreviousApplicationStatusNotFoundException : ApplicationNotFoundException(
    code = PROJECT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$PROJECT_ERROR_KEY_PREFIX.application.previous.status.not.found")
)
