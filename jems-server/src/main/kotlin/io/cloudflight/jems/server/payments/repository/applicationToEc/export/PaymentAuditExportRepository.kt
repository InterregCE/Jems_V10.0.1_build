package io.cloudflight.jems.server.payments.repository.applicationToEc.export

import io.cloudflight.jems.server.payments.entity.PaymentAuditExportEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PaymentAuditExportRepository : JpaRepository<PaymentAuditExportEntity, Long> {

    fun findAllByOrderByRequestTimeDesc(): List<PaymentAuditExportEntity>

    fun findAllByOrderByRequestTimeDesc(pageable: Pageable): Page<PaymentAuditExportEntity>

    fun findByPluginKeyAndProgrammeFundIdAndAccountingYearId(
        pluginKey: String,
        fundId: Long?,
        accoutingYearId: Long?
    ): Optional<PaymentAuditExportEntity>
}
