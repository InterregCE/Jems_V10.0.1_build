package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.art94Art95

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENTS_AVAILABLE_FOR_ART_94_ART_95_PREFIX = "S-GPAFA"
private const val GET_PAYMENTS_AVAILABLE_FOR_ART_94_ART_95_ERROR_KEY_PREFIX = "use.case.get.payments.available.for.art.94.art.95"
class GetPaymentsAvailableForArt94Art95Exception (cause: Throwable) : ApplicationException(
    code = GET_PAYMENTS_AVAILABLE_FOR_ART_94_ART_95_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENTS_AVAILABLE_FOR_ART_94_ART_95_ERROR_KEY_PREFIX.failded"),
    cause = cause
)
