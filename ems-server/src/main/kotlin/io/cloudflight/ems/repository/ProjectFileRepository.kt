package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.ProjectFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectFileRepository : PagingAndSortingRepository<ProjectFile, Long> {

    fun findAllByProject_Id(projectId: Long, pageable: Pageable): Page<ProjectFile>

}
