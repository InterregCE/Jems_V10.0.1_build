package io.cloudflight.ems.project.repository.description

import io.cloudflight.ems.project.entity.description.ProjectLongTermPlans
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectLongTermPlansRepository : PagingAndSortingRepository<ProjectLongTermPlans, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectLongTermPlans?

}
