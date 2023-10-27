package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeOverview

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CUMULATIVE_OVERVIEW_AMOUNTS_BY_PRIORITY_AXIS_PREFIX = "S-GECPCO"
private const val GET_CUMULATIVE_OVERVIEW_AMOUNTS_BY_PRIORITY_AXIS_PREFIX_ERROR_KEY_PREFIX =
    "use.case.get.ec.payment.cumulative.overview.amounts.by.priority.axis"

class GetCumulativeOverviewException (cause: Throwable) : ApplicationException(
    code = GET_CUMULATIVE_OVERVIEW_AMOUNTS_BY_PRIORITY_AXIS_PREFIX,
    i18nMessage = I18nMessage("$GET_CUMULATIVE_OVERVIEW_AMOUNTS_BY_PRIORITY_AXIS_PREFIX_ERROR_KEY_PREFIX.failed"),
    cause = cause
)