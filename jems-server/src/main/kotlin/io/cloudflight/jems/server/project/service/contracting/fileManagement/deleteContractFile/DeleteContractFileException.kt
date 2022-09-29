package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_CONTRACT_FILE_ERROR_CODE_PREFIX = "S-DELCF"
private const val DELETE_CONTRACT_FILE_ERROR_KEY_PREFIX = "use.case.delete.contract.file"

class DeleteContractFileException(cause: Throwable) : ApplicationException(
    code = DELETE_CONTRACT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_CONTRACT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

