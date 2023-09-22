package io.cloudflight.jems.server.project.repository.report.project.financialOverview.lumpSums

import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateLumpSumEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ReportProjectCertificateLumpSumRepository :
    JpaRepository<ReportProjectCertificateLumpSumEntity, Long> {

    fun findByReportEntityProjectIdAndReportEntityIdOrderByOrderNrAscIdAsc(
        projectId: Long,
        reportId: Long,
    ): List<ReportProjectCertificateLumpSumEntity>

    @Query("""
        SELECT new kotlin.Pair(
            lumpSum.orderNr,
            COALESCE(SUM(lumpSum.current), 0)
        )
        FROM #{#entityName} lumpSum
        WHERE lumpSum.reportEntity.id IN :reportIds
        GROUP BY lumpSum.orderNr
    """)
    fun findReportedCumulativeForReportIds(reportIds: Set<Long>): List<Pair<Int, BigDecimal>>


    @Query("""
        SELECT new kotlin.Pair(
            lumpSum.orderNr,
            COALESCE(SUM(lumpSum.currentVerified), 0)
        )
        FROM #{#entityName} lumpSum
        WHERE lumpSum.reportEntity.id IN :reportIds
        GROUP BY lumpSum.orderNr
    """)
    fun findVerifiedCumulativeForReportIds(reportIds: Set<Long>): List<Pair<Int, BigDecimal>>

}
