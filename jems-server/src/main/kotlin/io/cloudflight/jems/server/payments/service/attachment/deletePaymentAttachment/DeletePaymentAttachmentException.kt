package io.cloudflight.jems.server.payments.service.attachment.deletePaymentAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DELETE_PAYMENT_ATTACHMENT_ERROR_CODE_PREFIX = "S-DEPA"
private const val DELETE_PAYMENT_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.delete.payment.attachment"

class DeletePaymentAttachmentException(cause: Throwable) : ApplicationException(
    code = DELETE_PAYMENT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PAYMENT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DELETE_PAYMENT_ATTACHMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PAYMENT_ATTACHMENT_ERROR_KEY_PREFIX.not.found"),
)
