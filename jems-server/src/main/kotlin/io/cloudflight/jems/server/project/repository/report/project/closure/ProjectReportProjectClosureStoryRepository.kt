package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosureStoryEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportProjectClosureStoryRepository: JpaRepository<ProjectReportProjectClosureStoryEntity, Long> {

    @EntityGraph(value = "ProjectReportProjectClosureStoryEntity.withTranslations")
    fun getByReportId(reportId: Long): ProjectReportProjectClosureStoryEntity?
}
