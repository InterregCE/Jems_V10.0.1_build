package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectStatusHistoryRepository : PagingAndSortingRepository<ProjectStatusHistoryEntity, Long> {


    fun findTop2ByProjectIdOrderByUpdatedDesc(projectId: Long): List<ProjectStatusHistoryEntity>

}
