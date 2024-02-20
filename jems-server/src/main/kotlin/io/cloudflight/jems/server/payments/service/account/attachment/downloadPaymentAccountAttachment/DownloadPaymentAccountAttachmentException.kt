package io.cloudflight.jems.server.payments.service.account.attachment.downloadPaymentAccountAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DOWNLOAD_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_CODE_PREFIX = "S-DPAA"
private const val DOWNLOAD_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.download.payment.account.attachment"

class DownloadPaymentAccountAttachmentException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DOWNLOAD_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_PAYMENT_ACCOUNT_ATTACHMENT_ERROR_KEY_PREFIX.not.found"),
)
