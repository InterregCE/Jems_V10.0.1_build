package io.cloudflight.jems.server.programme.service.fund.getSelectedFund

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_SELECTED_FUND_ERROR_CODE_PREFIX = "S-GSF"
private const val GET_SELECTED_FUND_ERROR_KEY_PREFIX = "use.case.get.selected.funds"

class GetSelectedFundsException(cause: Throwable) : ApplicationException(
    code = GET_SELECTED_FUND_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_SELECTED_FUND_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
