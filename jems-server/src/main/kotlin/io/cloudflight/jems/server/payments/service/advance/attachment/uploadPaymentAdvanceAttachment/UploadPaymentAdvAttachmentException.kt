package io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPLOAD_PAYMENT_ADV_ATTACHMENT_ERROR_CODE_PREFIX = "S-UPAA"
private const val UPLOAD_PAYMENT_ADV_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.upload.payment.advance.attachment"

class UploadPaymentAdvAttachmentException(cause: Throwable) : ApplicationException(
    code = UPLOAD_PAYMENT_ADV_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPLOAD_PAYMENT_ADV_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class FileAlreadyExists : ApplicationUnprocessableException(
    code = "$UPLOAD_PAYMENT_ADV_ATTACHMENT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPLOAD_PAYMENT_ADV_ATTACHMENT_ERROR_KEY_PREFIX.file.already.exists"),
)

class FileTypeNotSupported : ApplicationUnprocessableException(
    code = "$UPLOAD_PAYMENT_ADV_ATTACHMENT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPLOAD_PAYMENT_ADV_ATTACHMENT_ERROR_KEY_PREFIX.type.not.supported")
)
