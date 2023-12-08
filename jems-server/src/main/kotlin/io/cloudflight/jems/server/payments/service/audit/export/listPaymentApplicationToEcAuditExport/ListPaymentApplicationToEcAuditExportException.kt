package io.cloudflight.jems.server.payments.service.audit.export.listPaymentApplicationToEcAuditExport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_CODE_PREFIX = "S-LPATECAE"
private const val LIST_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_KEY_PREFIX = "use.case.list.payment.application.to.ec.audit.export"

class ListPaymentApplicationToEcAuditExportException(cause: Throwable) : ApplicationException(
    code = LIST_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)