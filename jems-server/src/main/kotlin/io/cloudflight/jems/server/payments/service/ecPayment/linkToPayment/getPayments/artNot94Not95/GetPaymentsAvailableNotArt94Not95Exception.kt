package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENTS_AVAILABLE_NOT_ART_94_NOT_95_PREFIX = "S-GPANFA"
private const val GET_PAYMENTS_AVAILABLE_NOT_ART_94_NOT_95_ERROR_KEY_PREFIX = "use.case.get.payments.available.not.art.94.not.art.95"

class GetPaymentsAvailableNotArt94Not95Exception (cause: Throwable) : ApplicationException(
    code = GET_PAYMENTS_AVAILABLE_NOT_ART_94_NOT_95_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENTS_AVAILABLE_NOT_ART_94_NOT_95_ERROR_KEY_PREFIX.failded"),
    cause = cause
)
