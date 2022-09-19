package io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException


private const val GET_CONTRACT_INFO_ERROR_CODE_PREFIX = "S-GCI"
private const val GET_CONTRACT_INFO_ERROR_KEY_PREFIX = "use.case.get.project.contracting.contract"

class GetContractInfoException(cause: Throwable) : ApplicationException(
    code = GET_CONTRACT_INFO_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CONTRACT_INFO_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
