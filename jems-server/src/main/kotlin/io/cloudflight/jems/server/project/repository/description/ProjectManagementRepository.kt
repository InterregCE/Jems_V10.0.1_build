package io.cloudflight.jems.server.project.repository.description

import io.cloudflight.jems.server.project.entity.description.ProjectManagement
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectManagementRepository : PagingAndSortingRepository<ProjectManagement, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectManagement?

}
