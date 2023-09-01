package io.cloudflight.jems.server.project.repository.report.partner

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing.ReportCumulativeFund
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCumulativeFund
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportCoFinancingRepository :
    JpaRepository<ProjectPartnerReportCoFinancingEntity, ProjectPartnerReportCoFinancingIdEntity> {

    fun findAllByIdReportIdOrderByIdFundSortNumber(reportId: Long): List<ProjectPartnerReportCoFinancingEntity>

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing.ReportCumulativeFund(
            report.programmeFund.id,
            COALESCE(SUM(report.current), 0),
            COALESCE(SUM(report.currentParked), 0)
        )
        FROM #{#entityName} report
        WHERE report.id.report.id IN :reportIds
        GROUP BY report.programmeFund.id
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): List<ReportCumulativeFund>

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCumulativeFund(
            report.programmeFund.id,
            COALESCE(SUM(report.totalEligibleAfterControl), 0)
        )
        FROM #{#entityName} report
        WHERE report.id.report.id IN :reportIds
        GROUP BY report.programmeFund.id
    """)
    fun findCumulativeTotalsForReportIds(reportIds: Set<Long>): List<ProjectReportCumulativeFund>

    fun findAllByIdReportIdAndDisabledFalse(reportId: Long): List<ProjectPartnerReportCoFinancingEntity>

}
