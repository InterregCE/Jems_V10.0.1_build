package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportLumpSumEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ProjectPartnerReportLumpSumRepository : JpaRepository<PartnerReportLumpSumEntity, Long> {
    fun findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(
        partnerId: Long,
        reportId: Long,
    ): MutableList<PartnerReportLumpSumEntity>

    @Query("""
        SELECT new kotlin.Pair(
            lumpSum.orderNr,
            COALESCE(SUM(lumpSum.current), 0)
        )
        FROM #{#entityName} lumpSum
        WHERE lumpSum.reportEntity.id IN :reportIds
        GROUP BY lumpSum.orderNr
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): List<Pair<Int, BigDecimal>>

}
