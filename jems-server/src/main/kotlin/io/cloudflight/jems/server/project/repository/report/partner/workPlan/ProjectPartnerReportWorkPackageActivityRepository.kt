package io.cloudflight.jems.server.project.repository.report.partner.workPlan

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportWorkPackageActivityRepository :
    JpaRepository<ProjectPartnerReportWorkPackageActivityEntity, Long> {

    @EntityGraph(value = "ProjectPartnerReportWorkPackageActivityEntity.withTranslations")
    fun findAllByWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectPartnerReportEntity,
    ): MutableList<ProjectPartnerReportWorkPackageActivityEntity>

    @Query("""
        SELECT CASE WHEN COUNT(e) >= 1 THEN TRUE ELSE FALSE END FROM #{#entityName} e
            WHERE e.id = :activityId
                AND e.workPackageEntity.id = :workPackageId
                AND e.workPackageEntity.reportEntity.id = :reportId
                AND e.workPackageEntity.reportEntity.partnerId = :partnerId
    """)
    fun existsByActivityId(activityId: Long, workPackageId: Long, reportId: Long, partnerId: Long): Boolean

}
