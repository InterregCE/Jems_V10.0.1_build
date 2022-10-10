package io.cloudflight.jems.server.payments.service.attachment.getPaymentAttchament

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENT_ATTACHMENT_ERROR_CODE_PREFIX = "S-GPA"
private const val GET_PAYMENT_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.get.payment.attachment"

class GetPaymentAttachmentException(cause: Throwable) : ApplicationException(
    code = GET_PAYMENT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
