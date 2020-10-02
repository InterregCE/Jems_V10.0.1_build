package io.cloudflight.ems.project.repository.description

import io.cloudflight.ems.project.entity.description.ProjectOverallObjective
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectOverallObjectiveRepository : PagingAndSortingRepository<ProjectOverallObjective, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectOverallObjective?

}
