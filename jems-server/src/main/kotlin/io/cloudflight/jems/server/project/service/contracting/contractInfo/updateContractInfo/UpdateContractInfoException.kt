package io.cloudflight.jems.server.project.service.contracting.contractInfo.updateContractInfo

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException


private const val UPDATE_CONTRACT_INFO_ERROR_CODE_PREFIX = "S-UCI"
private const val UPDATE_CONTRACT_INFO_ERROR_KEY_PREFIX = "use.case.update.project.contracting.contract"

class UpdateContractInfoException(cause: Throwable): ApplicationException(
    code = UPDATE_CONTRACT_INFO_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CONTRACT_INFO_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
