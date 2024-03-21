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

    fun findAllByReportEntityIdIn(reportIds: Set<Long>): List<PartnerReportUnitCostEntity>
    @Query("""
        SELECT new kotlin.Triple(
            unitCost.programmeUnitCost.id,
            COALESCE(SUM(unitCost.current), 0),
            COALESCE(SUM(unitCost.currentParked), 0)
        )
        FROM #{#entityName} unitCost
        WHERE unitCost.reportEntity.id IN :reportIds
        GROUP BY unitCost.programmeUnitCost.id
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): List<Triple<Long, BigDecimal, BigDecimal>>


    @Query("""
        SELECT new kotlin.Pair(
            unitCost.programmeUnitCost.id,
            COALESCE(SUM(unitCost.currentParkedVerification), 0)
        )
        FROM #{#entityName} unitCost
        WHERE unitCost.reportEntity.partnerId=:partnerId AND unitCost.reportEntity.projectReport.id IN :projectReportIds
        GROUP BY unitCost.programmeUnitCost.id
    """)
    fun findVerificationParkedCumulativeForProjectReportIds(partnerId:Long, projectReportIds: Set<Long>): List<Pair<Long, BigDecimal>>

    @Query("""
        SELECT new kotlin.Pair(
            unitCost.programmeUnitCost.id,
            COALESCE(SUM(unitCost.totalEligibleAfterControl), 0)
        )
        FROM #{#entityName} unitCost
        WHERE unitCost.reportEntity.id IN :reportIds
        GROUP BY unitCost.programmeUnitCost.id
    """)
    fun findCumulativeForReportIdsAfterControl(reportIds: Set<Long>): List<Pair<Long, BigDecimal>>

}
