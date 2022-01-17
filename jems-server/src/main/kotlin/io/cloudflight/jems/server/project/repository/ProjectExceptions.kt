package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val PROJECT_ERROR_CODE_PREFIX = "P-PA"
const val PROJECT_ERROR_KEY_PREFIX = "project"

class PreviousApplicationStatusNotFoundException : ApplicationNotFoundException(
    code = "$PROJECT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$PROJECT_ERROR_KEY_PREFIX.application.previous.status.not.found")
)
class ApplicationStatusNotFoundException : ApplicationNotFoundException(
    code = "$PROJECT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$PROJECT_ERROR_KEY_PREFIX.application.status.not.found")
)
class ApplicationVersionNotFoundException : ApplicationNotFoundException(
    code = "$PROJECT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$PROJECT_ERROR_KEY_PREFIX.application.version.not.found")
)

class ProjectNotFoundException : ApplicationNotFoundException(
    code = "$PROJECT_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$PROJECT_ERROR_KEY_PREFIX.not.found")
)

class ProjectRestoreTimestampNotFoundException : ApplicationNotFoundException(
    code = "$PROJECT_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$PROJECT_ERROR_KEY_PREFIX.restore.version.not.found")
)
