package io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CO_FINANCING_ERROR_CODE_PREFIX = "S-GPPCF"
private const val GET_CO_FINANCING_ERROR_KEY_PREFIX = "use.case.get.cofinancing"

class GetCoFinancingException(cause: Throwable) : ApplicationException(
    code = GET_CO_FINANCING_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CO_FINANCING_ERROR_KEY_PREFIX.failed"),
    cause = cause
)