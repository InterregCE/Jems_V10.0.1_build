package io.cloudflight.jems.server.project.repository.description

import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPeriodRepository : PagingAndSortingRepository<ProjectPeriodEntity, ProjectPeriodId> {

    fun findByIdProjectIdAndIdNumber(projectId: Long, number: Int): ProjectPeriodEntity

}
