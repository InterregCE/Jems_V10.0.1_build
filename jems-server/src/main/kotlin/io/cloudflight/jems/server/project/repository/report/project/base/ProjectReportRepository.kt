package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
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

    fun findAllByProjectIdAndStatusInOrderByNumberDesc(projectId: Long, statuses: Set<ProjectReportStatus>): List<ProjectReportEntity>

    fun findAllByProjectIdAndDeadlineNotNull(projectId: Long): List<ProjectReportEntity>

    fun findAllByProjectIdAndDeadlineId(projectId: Long, deadlineId: Long): List<ProjectReportEntity>

}
