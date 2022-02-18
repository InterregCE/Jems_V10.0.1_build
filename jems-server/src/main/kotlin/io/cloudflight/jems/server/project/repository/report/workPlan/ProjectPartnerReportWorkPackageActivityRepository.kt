package io.cloudflight.jems.server.project.repository.report.workPlan

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportWorkPackageActivityRepository :
    JpaRepository<ProjectPartnerReportWorkPackageActivityEntity, Long> {

    @EntityGraph(value = "ProjectPartnerReportWorkPackageActivityEntity.withTranslations")
    fun findAllByWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectPartnerReportEntity,
    ): MutableList<ProjectPartnerReportWorkPackageActivityEntity>
}
