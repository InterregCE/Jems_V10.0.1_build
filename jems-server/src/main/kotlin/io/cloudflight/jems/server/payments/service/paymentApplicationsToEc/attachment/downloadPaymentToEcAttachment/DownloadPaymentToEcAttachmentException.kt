package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.downloadPaymentToEcAttachment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val DOWNLOAD_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX = "S-DPTEA"
private const val DOWNLOAD_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX = "use.case.download.payment.to.ecattachment"

class DownloadPaymentToEcAttachmentException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class FileNotFound : ApplicationNotFoundException(
    code = "$DOWNLOAD_PAYMENT_TO_EC_ATTACHMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DOWNLOAD_PAYMENT_TO_EC_ATTACHMENT_ERROR_KEY_PREFIX.not.found"),
)
