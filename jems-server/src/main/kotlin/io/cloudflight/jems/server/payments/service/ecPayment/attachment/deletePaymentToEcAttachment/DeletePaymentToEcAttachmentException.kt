package io.cloudflight.jems.server.payments.service.ecPayment.attachment.deletePaymentToEcAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX = "S-DEPTEA"
private const val DELETE_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.delete.payment.to.ec.attachment"

class DeletePaymentToEcAttachmentException(cause: Throwable) : ApplicationException(
    code = DELETE_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
