package io.cloudflight.jems.server.project.repository.report.project.workPlan

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageInvestmentEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportWorkPackageInvestmentRepository :
    JpaRepository<ProjectReportWorkPackageInvestmentEntity, Long> {
    @EntityGraph(value = "ProjectReportWorkPackageInvestmentEntity.withTranslations")
    fun findAllByWorkPackageEntityReportEntityOrderByNumber(
        reportEntity: ProjectReportEntity,
    ): MutableList<ProjectReportWorkPackageInvestmentEntity>
}
