package io.cloudflight.jems.server.payments.service.audit.export.attachment.deletePaymentAuditAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_PAYMENT_AUDIT_ATTACHMENT_ERROR_CODE_PREFIX = "S-DEPAA"
private const val DELETE_PAYMENT_AUDIT_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.delete.payment.audit.attachment"

class DeletePaymentAuditAttachmentException(cause: Throwable) : ApplicationException(
    code = DELETE_PAYMENT_AUDIT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PAYMENT_AUDIT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
