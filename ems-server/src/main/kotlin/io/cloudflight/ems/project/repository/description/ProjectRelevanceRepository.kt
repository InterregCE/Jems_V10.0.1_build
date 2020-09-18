package io.cloudflight.ems.project.repository.description

import io.cloudflight.ems.project.entity.description.ProjectRelevance
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRelevanceRepository : PagingAndSortingRepository<ProjectRelevance, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectRelevance?

}
