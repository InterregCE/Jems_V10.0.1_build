package io.cloudflight.ems.project.repository.description

import io.cloudflight.ems.project.entity.description.ProjectPartnership
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnershipRepository : PagingAndSortingRepository<ProjectPartnership, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectPartnership?

}
