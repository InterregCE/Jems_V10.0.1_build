package io.cloudflight.jems.server.payments.service.audit.export.listPaymentApplicationToEcAuditExport

import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import org.springframework.data.domain.Page

interface ListPaymentApplicationToEcAuditExportInteractor {
    fun list(): Page<PaymentToEcExportMetadata>
}
