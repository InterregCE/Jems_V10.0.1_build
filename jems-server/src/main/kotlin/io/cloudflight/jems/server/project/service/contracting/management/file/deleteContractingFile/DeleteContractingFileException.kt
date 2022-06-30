package io.cloudflight.jems.server.project.service.contracting.management.file.deleteContractingFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DELETE_CONTRACTING_FILE_ERROR_CODE_PREFIX = "S-DCF"
private const val DELETE_CONTRACTING_FILE_ERROR_KEY_PREFIX = "use.case.delete.contracting.file"

class DeleteContractingFileException(cause: Throwable) : ApplicationException(
    code = DELETE_CONTRACTING_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_CONTRACTING_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DELETE_CONTRACTING_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_CONTRACTING_FILE_ERROR_KEY_PREFIX.not.found"),
)
