package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.getReconciliationOverview

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val ERROR_CODE_PREFIX = "S-GRO"
private const val ERROR_KEY_PREFIX = "use.case.get.reconciliation.overview"

class GetReconciliationOverviewException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
