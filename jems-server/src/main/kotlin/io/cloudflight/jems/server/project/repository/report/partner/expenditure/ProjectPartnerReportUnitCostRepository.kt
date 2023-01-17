package io.cloudflight.jems.server.project.repository.report.partner.expenditure

import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportUnitCostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ProjectPartnerReportUnitCostRepository : JpaRepository<PartnerReportUnitCostEntity, Long> {

    fun findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(
        partnerId: Long,
        reportId: Long,
    ): MutableList<PartnerReportUnitCostEntity>

    fun findByReportEntityIdAndProgrammeUnitCostId(reportId: Long, programmeUnitCostId: Long): PartnerReportUnitCostEntity

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
