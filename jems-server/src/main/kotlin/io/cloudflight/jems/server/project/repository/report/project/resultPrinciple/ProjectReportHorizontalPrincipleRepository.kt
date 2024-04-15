package io.cloudflight.jems.server.project.repository.report.project.resultPrinciple

import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportHorizontalPrincipleEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportHorizontalPrincipleRepository: JpaRepository<ProjectReportHorizontalPrincipleEntity, Long> {

    @EntityGraph(value = "ProjectReportHorizontalPrincipleEntity.withTranslations")
    fun getByProjectReportId(reportId: Long): ProjectReportHorizontalPrincipleEntity

}
