package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getPayments.ftls.artNot94Not95

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENTS_AVAILABLE_FOR_ART_NOT_94_NOT_95_PREFIX = "S-GPAFAN"
private const val GET_PAYMENTS_AVAILABLE_FOR_ART_NOT_94_NOT_95_ERROR_KEY_PREFIX = "use.case.get.payments.available.for.art.not.94.not.95"

class GetFtlsPaymentsAvailableForArtNot94Not95Exception(cause: Throwable) : ApplicationException(
    code = GET_PAYMENTS_AVAILABLE_FOR_ART_NOT_94_NOT_95_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENTS_AVAILABLE_FOR_ART_NOT_94_NOT_95_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
