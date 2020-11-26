package io.cloudflight.jems.server.project.repository.description

import io.cloudflight.jems.server.project.entity.ProjectPeriod
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPeriodRepository : PagingAndSortingRepository<ProjectPeriod, ProjectPeriodId> {

    fun findByIdProjectIdAndIdNumber(projectId: Long, number: Int): ProjectPeriod

}