package io.cloudflight.jems.server.project.repository.report.workPlan

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageEntity
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
