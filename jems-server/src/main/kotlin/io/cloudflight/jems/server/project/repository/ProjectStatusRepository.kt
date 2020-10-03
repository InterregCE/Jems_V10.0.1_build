package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.project.entity.ProjectStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectStatusRepository : PagingAndSortingRepository<ProjectStatus, Long> {

    fun findFirstByProjectIdAndStatusNotInOrderByUpdatedDesc(
        projectId: Long,
        ignoreStatuses: Collection<ProjectApplicationStatus>
    ): ProjectStatus?

    fun findTop2ByProjectIdOrderByUpdatedDesc(projectId: Long): List<ProjectStatus>

}
