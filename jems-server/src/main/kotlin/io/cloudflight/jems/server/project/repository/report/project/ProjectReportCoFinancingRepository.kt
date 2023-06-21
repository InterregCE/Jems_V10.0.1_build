package io.cloudflight.jems.server.project.repository.report.project

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCumulativeFund
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportCoFinancingRepository :
    JpaRepository<ProjectReportCoFinancingEntity, ProjectReportCoFinancingIdEntity> {

    fun findAllByIdReportIdOrderByIdFundSortNumber(reportId: Long): List<ProjectReportCoFinancingEntity>

    @Query("""
        SELECT new io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCumulativeFund(
            report.programmeFund.id,
            COALESCE(SUM(report.current), 0)
        )
        FROM #{#entityName} report
        WHERE report.id.report.id IN :reportIds
        GROUP BY report.programmeFund.id
    """)
    fun findCumulativeForReportIds(reportIds: Set<Long>): List<ProjectReportCumulativeFund>

}
