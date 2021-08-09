package io.cloudflight.jems.server.project.repository.file

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val PROJECT_FILE_ERROR_CODE_PREFIX = "P-PF"
const val PROJECT_FILE_ERROR_KEY_PREFIX = "project.file"

class FileNameAlreadyExistsException : ApplicationUnprocessableException(
    code = "$PROJECT_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$PROJECT_FILE_ERROR_KEY_PREFIX.name.already.exists.in.the.category")
)
class ProjectFileNotFoundException : ApplicationUnprocessableException(
    code = "$PROJECT_FILE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$PROJECT_FILE_ERROR_KEY_PREFIX.not.found")
)
