package io.cloudflight.jems.server.payments.service.audit.export.generatePaymentApplicationToEcAuditExport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val GENERATE_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_CODE_PREFIX = "S-GPATECAE"
private const val GENERATE_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_KEY_PREFIX = "use.case.generate.payment.application.to.ec.audit.export"

class GeneratePaymentApplicationToEcAuditExportException(cause: Throwable) : ApplicationException(
    code = GENERATE_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GENERATE_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
class ExportInProgressException : ApplicationUnprocessableException(
    code = "$GENERATE_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GENERATE_PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT_ERROR_KEY_PREFIX.an.export.is.already.in.progress")
)
