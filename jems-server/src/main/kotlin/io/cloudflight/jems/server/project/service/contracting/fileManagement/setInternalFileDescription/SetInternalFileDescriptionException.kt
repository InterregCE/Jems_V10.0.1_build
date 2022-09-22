package io.cloudflight.jems.server.project.service.contracting.fileManagement.setInternalFileDescription

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val SET_DESCRIPTION_TO_INTERNAL_FILE_ERROR_CODE_PREFIX = "S-SDTIF"
private const val SET_DESCRIPTION_TO_INTERNAL_FILE_ERROR_KEY_PREFIX = "use.case.set.description.to.internal.file"

class SetDescriptionToInternalFileException(cause: Throwable) : ApplicationException(
    code = SET_DESCRIPTION_TO_INTERNAL_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_INTERNAL_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$SET_DESCRIPTION_TO_INTERNAL_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_INTERNAL_FILE_ERROR_KEY_PREFIX.not.found"),
)
