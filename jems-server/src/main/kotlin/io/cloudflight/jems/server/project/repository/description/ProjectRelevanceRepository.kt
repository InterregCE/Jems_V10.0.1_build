package io.cloudflight.jems.server.project.repository.description

import io.cloudflight.jems.server.project.entity.description.ProjectRelevance
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRelevanceRepository : PagingAndSortingRepository<ProjectRelevance, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectRelevance?

}
