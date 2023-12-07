package io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.getPaymentAuditAttchament

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PAYMENT_AUDIT_ATTACHMENT_ERROR_CODE_PREFIX = "S-GPAA"
private const val GET_PAYMENT_AUDIT_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.get.payment.audit.attachment"

class GetPaymentAuditAttachmentException(cause: Throwable) : ApplicationException(
    code = GET_PAYMENT_AUDIT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PAYMENT_AUDIT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
