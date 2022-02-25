package io.cloudflight.jems.server.project.repository.report.workPlan

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportWorkPackageActivityDeliverableRepository :
    JpaRepository<ProjectPartnerReportWorkPackageActivityDeliverableEntity, Long> {

    @EntityGraph(value = "ProjectPartnerReportWorkPackageActivityDeliverableEntity.withTranslations")
    fun findAllByActivityEntityWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectPartnerReportEntity,
    ): MutableList<ProjectPartnerReportWorkPackageActivityDeliverableEntity>
}
