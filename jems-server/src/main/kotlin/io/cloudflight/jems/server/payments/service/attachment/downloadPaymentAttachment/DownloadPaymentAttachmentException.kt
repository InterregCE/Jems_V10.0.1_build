package io.cloudflight.jems.server.payments.service.attachment.downloadPaymentAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DOWNLOAD_PAYMENT_ATTACHMENT_ERROR_CODE_PREFIX = "S-DPA"
private const val DOWNLOAD_PAYMENT_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.download.payment.attachment"

class DownloadPaymentAttachmentException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_PAYMENT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_PAYMENT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DOWNLOAD_PAYMENT_ATTACHMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_PAYMENT_ATTACHMENT_ERROR_KEY_PREFIX.not.found"),
)
