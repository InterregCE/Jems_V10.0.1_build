package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.getPaymentToEcAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX = "S-GPTEA"
private const val GET_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.get.payment.to.ec.attachment"

class GetPaymentToEcAttachmentException(cause: Throwable) : ApplicationException(
    code = GET_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
