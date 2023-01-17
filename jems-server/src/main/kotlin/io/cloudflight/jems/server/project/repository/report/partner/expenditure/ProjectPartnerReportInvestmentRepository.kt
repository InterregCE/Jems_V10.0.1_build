package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal

interface ProjectPartnerReportInvestmentRepository :
    JpaRepository<PartnerReportInvestmentEntity, Long> {

    fun findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(
        partnerId: Long,
        reportId: Long,
    ): MutableList<PartnerReportInvestmentEntity>

    fun findByReportEntityIdAndInvestmentId(reportId: Long, projectInvestmentId: Long): PartnerReportInvestmentEntity

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

}
