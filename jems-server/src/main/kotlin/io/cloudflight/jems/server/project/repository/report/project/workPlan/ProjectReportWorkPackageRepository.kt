package io.cloudflight.jems.server.project.repository.report.project.workPlan

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportWorkPackageRepository : JpaRepository<ProjectReportWorkPackageEntity, Long> {

    @EntityGraph(value = "ProjectReportWorkPackageEntity.withTranslations")
    fun findAllByReportEntityOrderByNumber(
        reportEntity: ProjectReportEntity,
    ): MutableList<ProjectReportWorkPackageEntity>

    fun deleteAllByReportEntity(reportEntity: ProjectReportEntity)

}
