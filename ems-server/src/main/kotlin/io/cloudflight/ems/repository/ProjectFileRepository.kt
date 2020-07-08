package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.ProjectFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProjectFileRepository : PagingAndSortingRepository<ProjectFile, Long> {

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<ProjectFile>

    fun findFirstByProjectIdAndId(projectId: Long, id: Long): Optional<ProjectFile>

    fun findFirstByProjectIdAndName(projectId: Long, name: String): Optional<ProjectFile>

}
