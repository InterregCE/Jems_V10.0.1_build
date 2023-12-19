package io.cloudflight.jems.server.payments.repository.applicationToEc.export

import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcAuditExportEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PaymentApplicationToEcAuditExportRepository : JpaRepository<PaymentApplicationToEcAuditExportEntity, Long> {
    fun findAllByOrderByRequestTimeDesc(): List<PaymentApplicationToEcAuditExportEntity>
    fun findAllByOrderByRequestTimeDesc(pageable: Pageable): Page<PaymentApplicationToEcAuditExportEntity>

    fun findByPluginKeyAndProgrammeFundIdAndAccountingYearId(
        pluginKey: String,
        fundId: Long?,
        accoutingYearId: Long?
    ): Optional<PaymentApplicationToEcAuditExportEntity>
}
