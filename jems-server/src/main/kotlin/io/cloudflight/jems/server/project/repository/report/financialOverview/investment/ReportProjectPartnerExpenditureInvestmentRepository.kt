package io.cloudflight.jems.server.project.repository.report.financialOverview.investment

import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureInvestmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal

interface ReportProjectPartnerExpenditureInvestmentRepository :
    JpaRepository<ReportProjectPartnerExpenditureInvestmentEntity, Long> {

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

    fun findByReportEntityPartnerIdAndReportEntityIdOrderByInvestmentIdAscIdAsc(
        partnerId: Long,
        reportId: Long,
    ): MutableList<ReportProjectPartnerExpenditureInvestmentEntity>
}
