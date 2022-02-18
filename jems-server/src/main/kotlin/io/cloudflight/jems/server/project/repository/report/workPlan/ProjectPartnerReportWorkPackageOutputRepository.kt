package io.cloudflight.jems.server.project.repository.report.workPlan

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportWorkPackageOutputRepository :
    JpaRepository<ProjectPartnerReportWorkPackageOutputEntity, Long> {

    @EntityGraph(value = "ProjectPartnerReportWorkPackageOutputEntity.withTranslations")
    fun findAllByWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectPartnerReportEntity,
    ): MutableList<ProjectPartnerReportWorkPackageOutputEntity>
}
