package io.cloudflight.ems.project.repository.description

import io.cloudflight.ems.project.entity.description.ProjectManagement
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectManagementRepository : PagingAndSortingRepository<ProjectManagement, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectManagement?

}
