package io.cloudflight.jems.server.project.repository.report.project.workPlan

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.persistence.DeliverableCumulative
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportWorkPackageActivityDeliverableRepository :
    JpaRepository<ProjectReportWorkPackageActivityDeliverableEntity, Long> {

    @EntityGraph(value = "ProjectReportWorkPackageActivityDeliverableEntity.withTranslations")
    fun findAllByActivityEntityWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectReportEntity,
    ): MutableList<ProjectReportWorkPackageActivityDeliverableEntity>

    @Query("""
        SELECT CASE WHEN COUNT(e) >= 1 THEN TRUE ELSE FALSE END FROM #{#entityName} e
            WHERE e.id = :deliverableId
                AND e.activityEntity.id = :activityId
                AND e.activityEntity.workPackageEntity.id = :workPackageId
                AND e.activityEntity.workPackageEntity.reportEntity.id = :reportId
                AND e.activityEntity.workPackageEntity.reportEntity.projectId = :projectId
    """)
    fun existsByDeliverableId(deliverableId: Long, activityId: Long, workPackageId: Long, reportId: Long, projectId: Long): Boolean

    @Query("""
        SELECT new io.cloudflight.jems.server.project.service.report.model.project.workPlan.persistence.DeliverableCumulative(
            deliverable.activityEntity.workPackageEntity.number,
            deliverable.activityEntity.number,
            deliverable.number,
            COALESCE(SUM(deliverable.currentReport), 0)
        )
        FROM #{#entityName} deliverable
        WHERE deliverable.activityEntity.workPackageEntity.reportEntity.id IN :reportIds
        GROUP BY deliverable.activityEntity.workPackageEntity.number, deliverable.activityEntity.number, deliverable.number
    """)
    fun getCumulativeValues(reportIds: Set<Long>): List<DeliverableCumulative>

}
