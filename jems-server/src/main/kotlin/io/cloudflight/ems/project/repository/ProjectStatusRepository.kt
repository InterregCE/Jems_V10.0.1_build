package io.cloudflight.ems.project.repository

import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.project.entity.ProjectStatus
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
