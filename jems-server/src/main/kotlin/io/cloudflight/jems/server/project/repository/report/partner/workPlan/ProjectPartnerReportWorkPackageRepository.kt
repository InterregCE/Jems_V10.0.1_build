package io.cloudflight.jems.server.project.repository.report.partner.workPlan

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportWorkPackageRepository : JpaRepository<ProjectPartnerReportWorkPackageEntity, Long> {

    @EntityGraph(value = "ProjectPartnerReportWorkPackageEntity.withTranslations")
    fun findAllByReportEntityOrderByNumber(
        reportEntity: ProjectPartnerReportEntity,
    ): MutableList<ProjectPartnerReportWorkPackageEntity>

}
