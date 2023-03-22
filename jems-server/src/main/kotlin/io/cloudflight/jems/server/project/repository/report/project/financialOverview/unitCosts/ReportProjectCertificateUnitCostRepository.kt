package io.cloudflight.jems.server.project.repository.report.project.financialOverview.unitCosts

import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateUnitCostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ReportProjectCertificateUnitCostRepository :
    JpaRepository<ReportProjectCertificateUnitCostEntity, Long> {

    fun findByReportEntityProjectIdAndReportEntityIdOrderByIdAsc(
        projectId: Long,
        reportId: Long,
    ): MutableList<ReportProjectCertificateUnitCostEntity>

    @Query("""
        SELECT new kotlin.Pair(
            unitCost.programmeUnitCost.id,
            COALESCE(SUM(unitCost.current), 0)
        )
        FROM #{#entityName} unitCost
        WHERE unitCost.reportEntity.id IN :reportIds
        GROUP BY unitCost.programmeUnitCost.id
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): List<Pair<Long, BigDecimal>>
}
