package io.cloudflight.ems.project.repository

import io.cloudflight.ems.project.entity.description.ProjectManagement
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectManagementRepository : PagingAndSortingRepository<ProjectManagement, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectManagement?

}
