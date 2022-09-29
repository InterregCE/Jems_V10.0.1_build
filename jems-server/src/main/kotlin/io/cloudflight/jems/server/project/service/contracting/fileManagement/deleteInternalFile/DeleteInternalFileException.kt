package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteInternalFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_CONTRACTING_INTERNAL_FILE_ERROR_CODE_PREFIX = "S-DELCIF"
private const val DELETE_CONTRACTING_INTERNAL_FILE_ERROR_KEY_PREFIX = "use.case.delete.contracting.internal.file"

class DeleteContractingInternalFileException(cause: Throwable): ApplicationException(
    code = DELETE_CONTRACTING_INTERNAL_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_CONTRACTING_INTERNAL_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
