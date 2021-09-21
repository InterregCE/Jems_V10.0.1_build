package io.cloudflight.jems.server.project.service.file.delete_project_file

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PROJECT_FILE_ERROR_CODE_PREFIX = "S-DPF"
private const val DELETE_PROJECT_FILE_ERROR_KEY_PREFIX = "use.case.delete.project.file"

class DeleteProjectFileException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_FILE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class DeletingOldFileFromApplicationCategoryIsNotAllowedException : ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROJECT_FILE_ERROR_KEY_PREFIX.from.updated.application.is.not.allowed")
)
