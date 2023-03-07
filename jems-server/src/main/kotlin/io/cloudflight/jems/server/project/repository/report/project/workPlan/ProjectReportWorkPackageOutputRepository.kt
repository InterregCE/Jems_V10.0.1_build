package io.cloudflight.jems.server.project.repository.report.project.workPlan

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.persistence.OutputCumulative
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportWorkPackageOutputRepository :
    JpaRepository<ProjectReportWorkPackageOutputEntity, Long> {

    @EntityGraph(value = "ProjectReportWorkPackageOutputEntity.withTranslations")
    fun findAllByWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectReportEntity,
    ): MutableList<ProjectReportWorkPackageOutputEntity>

    @Query("""
        SELECT CASE WHEN COUNT(e) >= 1 THEN TRUE ELSE FALSE END FROM #{#entityName} e
            WHERE e.id = :outputId
                AND e.workPackageEntity.id = :workPackageId
                AND e.workPackageEntity.reportEntity.id = :reportId
                AND e.workPackageEntity.reportEntity.projectId = :projectId
    """)
    fun existsByOutputId(outputId: Long, workPackageId: Long, reportId: Long, projectId: Long): Boolean

    @Query("""
        SELECT new io.cloudflight.jems.server.project.service.report.model.project.workPlan.persistence.OutputCumulative(
            output.workPackageEntity.number,
            output.number,
            COALESCE(SUM(output.currentReport), 0)
        )
        FROM #{#entityName} output
        WHERE output.workPackageEntity.reportEntity.id IN :reportIds
        GROUP BY output.workPackageEntity.number, output.number
    """)
    fun getCumulativeValues(reportIds: Set<Long>): List<OutputCumulative>

}
