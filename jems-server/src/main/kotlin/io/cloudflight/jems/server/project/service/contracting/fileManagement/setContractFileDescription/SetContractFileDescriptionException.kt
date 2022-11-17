package io.cloudflight.jems.server.project.service.contracting.fileManagement.setContractFileDescription

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val SET_DESCRIPTION_TO_CONTRACT_FILE_ERROR_CODE_PREFIX = "S-SDTCF"
private const val SET_DESCRIPTION_TO_CONTRACT_FILE_ERROR_KEY_PREFIX = "use.case.set.description.to.project.contract.file"

class SetDescriptionToContractFileException(cause: Throwable) : ApplicationException(
    code = SET_DESCRIPTION_TO_CONTRACT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_CONTRACT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$SET_DESCRIPTION_TO_CONTRACT_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_CONTRACT_FILE_ERROR_KEY_PREFIX.not.found"),
)
