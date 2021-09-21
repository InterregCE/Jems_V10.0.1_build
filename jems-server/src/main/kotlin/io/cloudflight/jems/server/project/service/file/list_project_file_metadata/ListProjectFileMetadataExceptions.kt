package io.cloudflight.jems.server.project.service.file.list_project_file_metadata

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_PROJECT_FILE_METADATA_ERROR_CODE_PREFIX = "S-LPFM"
private const val LIST_PROJECT_FILE_METADATA_ERROR_KEY_PREFIX = "use.case.list.project.file.metadata"

class ListProjectFileMetadataExceptions(cause: Throwable) : ApplicationException(
    code = LIST_PROJECT_FILE_METADATA_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PROJECT_FILE_METADATA_ERROR_KEY_PREFIX.failed"), cause = cause
)
