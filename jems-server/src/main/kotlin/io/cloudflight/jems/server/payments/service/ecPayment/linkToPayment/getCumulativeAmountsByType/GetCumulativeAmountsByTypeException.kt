package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsByType

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CUMULATIVE_AMOUNTS_BY_PRIORITY_AXIS_PREFIX = "S-GCABPA"
private const val GET_CUMULATIVE_AMOUNTS_BY_PRIORITY_AXIS_PREFIX_ERROR_KEY_PREFIX = "use.case.get.cumulative.amounts.by.priority.axis"

class GetCumulativeAmountsByTypeException(cause: Throwable) : ApplicationException(
    code = GET_CUMULATIVE_AMOUNTS_BY_PRIORITY_AXIS_PREFIX,
    i18nMessage = I18nMessage("$GET_CUMULATIVE_AMOUNTS_BY_PRIORITY_AXIS_PREFIX_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
