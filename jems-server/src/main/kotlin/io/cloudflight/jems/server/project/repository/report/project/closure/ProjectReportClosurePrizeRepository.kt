package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportClosurePrizeEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportClosurePrizeRepository: JpaRepository<ProjectReportClosurePrizeEntity, Long> {

    @EntityGraph(value = "ProjectReportProjectClosurePrizeEntity.withTranslations")
    fun findAllByReportIdOrderBySortNumberAsc(reportId: Long): MutableList<ProjectReportClosurePrizeEntity>

    fun deleteAllByReportId(reportId: Long)
}
