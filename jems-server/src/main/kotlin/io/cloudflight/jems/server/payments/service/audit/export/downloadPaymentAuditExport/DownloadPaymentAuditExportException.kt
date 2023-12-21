package io.cloudflight.jems.server.payments.service.audit.export.downloadPaymentAuditExport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DOWNLOAD_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_CODE_PREFIX = "S-DPATECAE"
private const val DOWNLOAD_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_CODE_ERROR_KEY_PREFIX = "use.case.download.payment.application.to.ec.audit.export"

class DownloadPaymentAuditExportException(cause: Throwable) : ApplicationException(
    code = DOWNLOAD_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DOWNLOAD_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_CODE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
