package io.cloudflight.jems.server.project.repository.report.project.financialOverview.investment

import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateInvestmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ReportProjectCertificateInvestmentRepository :
    JpaRepository<ReportProjectCertificateInvestmentEntity, Long> {

    fun findByReportEntityProjectIdAndReportEntityIdOrderByIdAsc(
        projectId: Long,
        reportId: Long,
    ): MutableList<ReportProjectCertificateInvestmentEntity>

    @Query("""
        SELECT new kotlin.Pair(
            investment.investmentId,
            COALESCE(SUM(investment.current), 0)
        )
        FROM #{#entityName} investment
        WHERE investment.reportEntity.id IN :reportIds
        GROUP BY investment.investmentId
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): List<Pair<Long, BigDecimal>>


    @Query("""
        SELECT new kotlin.Pair(
            investment.investmentId,
            COALESCE(SUM(investment.currentVerified), 0)
        )
        FROM #{#entityName} investment
        WHERE investment.reportEntity.id IN :reportIds
        GROUP BY investment.investmentId
    """)
    fun findVerifiedCumulativeForReportIds(reportIds: Set<Long>): List<Pair<Long, BigDecimal>>
}
