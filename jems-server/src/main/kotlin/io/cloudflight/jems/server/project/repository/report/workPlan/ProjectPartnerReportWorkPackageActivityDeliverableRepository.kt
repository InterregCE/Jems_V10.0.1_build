package io.cloudflight.jems.server.project.repository.report.workPlan

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportWorkPackageActivityDeliverableRepository :
    JpaRepository<ProjectPartnerReportWorkPackageActivityDeliverableEntity, Long> {

    @EntityGraph(value = "ProjectPartnerReportWorkPackageActivityDeliverableEntity.withTranslations")
    fun findAllByActivityEntityWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectPartnerReportEntity,
    ): MutableList<ProjectPartnerReportWorkPackageActivityDeliverableEntity>

    @Query("""
        SELECT CASE WHEN COUNT(e) >= 1 THEN TRUE ELSE FALSE END FROM #{#entityName} e
            WHERE e.id = :deliverableId
                AND e.activityEntity.id = :activityId
                AND e.activityEntity.workPackageEntity.id = :workPackageId
                AND e.activityEntity.workPackageEntity.reportEntity.id = :reportId
                AND e.activityEntity.workPackageEntity.reportEntity.partnerId = :partnerId
    """)
    fun existsByDeliverableId(deliverableId: Long, activityId: Long, workPackageId: Long, reportId: Long, partnerId: Long): Boolean

}
