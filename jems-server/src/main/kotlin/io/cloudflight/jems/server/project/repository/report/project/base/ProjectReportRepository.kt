package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportRepository : JpaRepository<ProjectReportEntity, Long> {

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<ProjectReportEntity>

    fun getByIdAndProjectId(id: Long, projectId: Long): ProjectReportEntity

    fun deleteByProjectIdAndId(projectId: Long, id: Long)

    fun findFirstByProjectIdOrderByIdDesc(projectId: Long): ProjectReportEntity?

    fun countAllByProjectId(projectId: Long): Int

    @Query(
        """
            SELECT report.id
            FROM #{#entityName} report
            WHERE report.status = 'Submitted' AND report.projectId = :projectId
        """
    )
    fun getSubmittedProjectReportIds(projectId: Long): Set<Long>
}
