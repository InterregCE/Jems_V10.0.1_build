package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95.regular

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_REGULAR_AVAILABLE_FOR_ART_NOT_94_NOT_95_PREFIX = "S-GRPAFAN"
private const val GET_REGULAR_AVAILABLE_FOR_ART_NOT_94_NOT_95_ERROR_KEY_PREFIX = "use.case.get.regular.payments.available.for.art.not.94.not.95"

class GetRegularPaymentsAvailableForArtNot94Not95Exception(cause: Throwable) : ApplicationException(
    code = GET_REGULAR_AVAILABLE_FOR_ART_NOT_94_NOT_95_PREFIX,
    i18nMessage = I18nMessage("$GET_REGULAR_AVAILABLE_FOR_ART_NOT_94_NOT_95_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
