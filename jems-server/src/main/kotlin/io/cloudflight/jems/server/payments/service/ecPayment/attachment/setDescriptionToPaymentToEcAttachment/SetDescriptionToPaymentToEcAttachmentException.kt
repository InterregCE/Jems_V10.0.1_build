package io.cloudflight.jems.server.payments.service.ecPayment.attachment.setDescriptionToPaymentToEcAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val SET_DESCRIPTION_TO_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX = "S-SDTPTEA"
private const val SET_DESCRIPTION_TO_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX =
    "use.case.set.description.to.payment.to.ec.attachment"

class SetDescriptionToPaymentToEcAttachmentException(cause: Throwable) : ApplicationException(
    code = SET_DESCRIPTION_TO_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class FileNotFound : ApplicationNotFoundException(
    code = "$SET_DESCRIPTION_TO_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX.not.found"),
)
