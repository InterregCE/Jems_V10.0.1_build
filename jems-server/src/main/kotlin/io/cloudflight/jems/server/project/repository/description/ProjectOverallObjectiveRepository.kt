package io.cloudflight.jems.server.project.repository.description

import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjective
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectOverallObjectiveRepository : PagingAndSortingRepository<ProjectOverallObjective, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectOverallObjective?

}
