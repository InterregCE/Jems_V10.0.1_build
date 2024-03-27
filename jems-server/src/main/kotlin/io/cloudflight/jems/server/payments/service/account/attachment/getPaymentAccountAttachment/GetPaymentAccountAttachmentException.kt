package io.cloudflight.jems.server.payments.service.account.attachment.getPaymentAccountAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_CODE_PREFIX = "S-GPAA"
private const val GET_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.get.payment.account.attachment"

class GetPaymentAccountAttachmentException(cause: Throwable) : ApplicationException(
    code = GET_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
