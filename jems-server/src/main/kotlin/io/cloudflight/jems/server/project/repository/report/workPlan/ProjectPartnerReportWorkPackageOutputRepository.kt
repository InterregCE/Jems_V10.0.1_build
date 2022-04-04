package io.cloudflight.jems.server.project.repository.report.workPlan

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportWorkPackageOutputRepository :
    JpaRepository<ProjectPartnerReportWorkPackageOutputEntity, Long> {

    @EntityGraph(value = "ProjectPartnerReportWorkPackageOutputEntity.withTranslations")
    fun findAllByWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectPartnerReportEntity,
    ): MutableList<ProjectPartnerReportWorkPackageOutputEntity>

    @Query("""
        SELECT CASE WHEN COUNT(e) >= 1 THEN TRUE ELSE FALSE END FROM #{#entityName} e
            WHERE e.id = :outputId
                AND e.workPackageEntity.id = :workPackageId
                AND e.workPackageEntity.reportEntity.id = :reportId
                AND e.workPackageEntity.reportEntity.partnerId = :partnerId
    """)
    fun existsByOutputId(outputId: Long, workPackageId: Long, reportId: Long, partnerId: Long): Boolean

}
