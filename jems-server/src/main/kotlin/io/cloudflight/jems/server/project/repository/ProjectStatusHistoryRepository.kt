package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectStatusHistoryRepository : PagingAndSortingRepository<ProjectStatusHistoryEntity, Long> {

    fun findTop2ByProjectIdOrderByUpdatedDesc(projectId: Long): List<ProjectStatusHistoryEntity>

    fun findAllByProjectIdAndStatusInOrderByUpdatedDesc(projectId: Long, statuses: List<ApplicationStatus>): List<ProjectStatusHistoryEntity>

    fun findFirstByProjectIdAndStatusInOrderByUpdatedDesc(projectId: Long, statuses: List<ApplicationStatus>): ProjectStatusHistoryEntity

    fun existsByStatus(status: ApplicationStatus): Boolean
}
