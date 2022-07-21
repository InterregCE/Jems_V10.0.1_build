package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing.ReportCumulativeFund
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportCoFinancingRepository :
    JpaRepository<ProjectPartnerReportCoFinancingEntity, ProjectPartnerReportCoFinancingIdEntity> {

    fun findAllByIdReportIdOrderByIdFundSortNumber(reportId: Long): List<ProjectPartnerReportCoFinancingEntity>

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing.ReportCumulativeFund(
            report.programmeFund.id,
            COALESCE(SUM(report.current), 0)
        )
        FROM #{#entityName} report
        WHERE report.id.report.id IN :reportIds
        GROUP BY report.programmeFund.id
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): List<ReportCumulativeFund>

}
