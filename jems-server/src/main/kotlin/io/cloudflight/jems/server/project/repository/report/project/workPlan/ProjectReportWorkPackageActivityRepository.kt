package io.cloudflight.jems.server.project.repository.report.project.workPlan

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportWorkPackageActivityRepository :
    JpaRepository<ProjectReportWorkPackageActivityEntity, Long> {

    @EntityGraph(value = "ProjectReportWorkPackageActivityEntity.withTranslations")
    fun findAllByWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectReportEntity,
    ): MutableList<ProjectReportWorkPackageActivityEntity>

    @Query("""
        SELECT CASE WHEN COUNT(e) >= 1 THEN TRUE ELSE FALSE END FROM #{#entityName} e
            WHERE e.id = :activityId
                AND e.workPackageEntity.id = :workPackageId
                AND e.workPackageEntity.reportEntity.id = :reportId
                AND e.workPackageEntity.reportEntity.projectId = :projectId
    """)
    fun existsByActivityId(activityId: Long, workPackageId: Long, reportId: Long, projectId: Long): Boolean

}
