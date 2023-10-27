package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getOverviewAmountsByType

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_OVERVIEW_AMOUNTS_BY_PRIORITY_AXIS_PREFIX = "S-GCABPA"
private const val GET_OVERVIEW_AMOUNTS_BY_PRIORITY_AXIS_PREFIX_ERROR_KEY_PREFIX = "use.case.get.overview.amounts.by.priority.axis"

class GetOverviewAmountsByTypeException(cause: Throwable) : ApplicationException(
    code = GET_OVERVIEW_AMOUNTS_BY_PRIORITY_AXIS_PREFIX,
    i18nMessage = I18nMessage("$GET_OVERVIEW_AMOUNTS_BY_PRIORITY_AXIS_PREFIX_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
