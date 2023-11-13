package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_LINKED_CORRECTION_PREFIX = "S-ULCEC"
private const val UPDATE_LINKED_CORRECTION_ERROR_KEY_PREFIX = "use.case.update.linked.correction.to.ec.payment"

class UpdateLinkedCorrectionException(cause: Throwable) : ApplicationException(
    code = UPDATE_LINKED_CORRECTION_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_LINKED_CORRECTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
