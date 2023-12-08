package io.cloudflight.jems.server.payments.repository.applicationToEc.export

import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcAuditExportEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentApplicationToEcAuditExportRepository : JpaRepository<PaymentApplicationToEcAuditExportEntity, Long> {
}
