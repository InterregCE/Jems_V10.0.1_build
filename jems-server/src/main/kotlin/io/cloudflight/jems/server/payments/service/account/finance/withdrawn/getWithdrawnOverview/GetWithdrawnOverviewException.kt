package io.cloudflight.jems.server.payments.service.account.finance.withdrawn.getWithdrawnOverview

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val ERROR_CODE_PREFIX = "S-GWO"
private const val ERROR_KEY_PREFIX = "use.case.get.withdrawn.overview"

class GetWithdrawnOverviewException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
