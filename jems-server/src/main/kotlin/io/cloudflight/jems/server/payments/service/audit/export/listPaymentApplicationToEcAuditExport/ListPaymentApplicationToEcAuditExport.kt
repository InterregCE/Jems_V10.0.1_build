package io.cloudflight.jems.server.payments.service.audit.export.listPaymentApplicationToEcAuditExport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAudit
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.payments.service.audit.export.PaymentApplicationToEcAuditExportPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListPaymentApplicationToEcAuditExport(
    private val paymentApplicationToEcAuditExportPersistence: PaymentApplicationToEcAuditExportPersistence,
) : ListPaymentApplicationToEcAuditExportInteractor {

    @CanRetrievePaymentsAudit
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListPaymentApplicationToEcAuditExportException::class)
    override fun list(): Page<PaymentToEcExportMetadata> =
        paymentApplicationToEcAuditExportPersistence.listPaymentApplicationToEcAuditExport(
            pageable = Pageable.unpaged(),
        )

}
