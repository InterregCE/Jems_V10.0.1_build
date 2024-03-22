package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportLumpSumEntity
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

    fun findAllByReportEntityIdIn(reportIds: Set<Long>): List<PartnerReportLumpSumEntity>

    fun findByReportEntityIdAndProgrammeLumpSumIdAndOrderNr(
        reportId: Long,
        programmeLumpSumId: Long,
        orderNr: Int,
    ): PartnerReportLumpSumEntity

    @Query("""
        SELECT new kotlin.Triple(
            lumpSum.orderNr,
            COALESCE(SUM(lumpSum.current), 0),
            COALESCE(SUM(lumpSum.currentParked), 0)
        )
        FROM #{#entityName} lumpSum
        WHERE lumpSum.reportEntity.id IN :reportIds
        GROUP BY lumpSum.orderNr
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): List<Triple<Int, BigDecimal, BigDecimal>>


    @Query("""
        SELECT new kotlin.Pair(
            lumpSum.orderNr,
            COALESCE(SUM(lumpSum.currentParkedVerification), 0)
        )
        FROM #{#entityName} lumpSum
        WHERE lumpSum.reportEntity.partnerId=:partnerId AND
                lumpSum.reportEntity.projectReport.id IN :projectReportIds
        GROUP BY lumpSum.orderNr
    """)
    fun findCumulativeVerificationParkedForProjectReportIds(partnerId: Long, projectReportIds: Set<Long>): List<Pair<Int, BigDecimal>>

    @Query("""
        SELECT new kotlin.Pair(
            lumpSum.orderNr,
            COALESCE(SUM(lumpSum.totalEligibleAfterControl), 0)
        )
        FROM #{#entityName} lumpSum
        WHERE lumpSum.reportEntity.id IN :reportIds
        GROUP BY lumpSum.orderNr
    """)
    fun findCumulativeAfterControlForReportIds(reportIds: Set<Long>): List<Pair<Int, BigDecimal>>

}
