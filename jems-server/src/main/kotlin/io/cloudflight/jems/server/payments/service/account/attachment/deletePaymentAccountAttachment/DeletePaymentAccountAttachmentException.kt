package io.cloudflight.jems.server.payments.service.account.attachment.deletePaymentAccountAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_CODE_PREFIX = "S-DEPAA"
private const val DELETE_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.delete.payment.account.attachment"

class DeletePaymentAccountAttachmentException(cause: Throwable) : ApplicationException(
    code = DELETE_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
