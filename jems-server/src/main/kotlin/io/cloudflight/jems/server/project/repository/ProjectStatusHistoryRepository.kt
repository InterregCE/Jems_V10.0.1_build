package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectStatusHistoryRepository : PagingAndSortingRepository<ProjectStatusHistoryEntity, Long> {

    fun findFirstByProjectIdAndStatusNotOrderByUpdatedDesc(
        projectId: Long,
        ignoreStatuses: ApplicationStatus
    ): ProjectStatusHistoryEntity?

    fun findTop2ByProjectIdOrderByUpdatedDesc(projectId: Long): List<ProjectStatusHistoryEntity>

}
