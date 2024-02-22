package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosurePrizeEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportProjectClosurePrizeRepository: JpaRepository<ProjectReportProjectClosurePrizeEntity, Long> {

    @EntityGraph(value = "ProjectReportProjectClosurePrizeEntity.withTranslations")
    fun findAllByReportId(reportId: Long): List<ProjectReportProjectClosurePrizeEntity>

    fun deleteAllByReportId(reportId: Long)
}
